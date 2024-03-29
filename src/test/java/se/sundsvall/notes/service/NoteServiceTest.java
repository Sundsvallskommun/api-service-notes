package se.sundsvall.notes.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.notes.api.model.CreateNoteRequest;
import se.sundsvall.notes.api.model.FindNotesRequest;
import se.sundsvall.notes.api.model.Note;
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.api.model.UpdateNoteRequest;
import se.sundsvall.notes.integration.db.NoteRepository;
import se.sundsvall.notes.integration.db.model.NoteEntity;
import se.sundsvall.notes.service.mapper.NoteMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.notes.service.ServiceConstants.ERROR_NOTE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

	@Mock
	private NoteRepository noteRepositoryMock;

	@Mock
	private RevisionService revisionServiceMock;

	@InjectMocks
	private NoteService noteService;

	@Test
	void createNote() {
		final var id = UUID.randomUUID().toString();
		final var createNoteRequestMock = Mockito.mock(CreateNoteRequest.class);
		final var noteEntity = NoteEntity.create().withId(id);
		final var note = Note.create().withId(id);

		final var currentRevision = Revision.create().withId(id).withVersion(1);

		// Mock
		when(noteRepositoryMock.save(any())).thenReturn(noteEntity);
		when(revisionServiceMock.createRevision(same(noteEntity))).thenReturn(currentRevision);

		try (MockedStatic<NoteMapper> mapperMock = Mockito.mockStatic(NoteMapper.class)) {
			mapperMock.when(() -> NoteMapper.toNoteEntity(any())).thenReturn(noteEntity);
			mapperMock.when(() -> NoteMapper.toNote(noteEntity)).thenReturn(note);

			// Call
			final var revisionInformation = noteService.createNote(createNoteRequestMock);

			// Verification
			mapperMock.verify(() -> NoteMapper.toNoteEntity(same(createNoteRequestMock)));
			verify(noteRepositoryMock).save(same(noteEntity));
			verify(revisionServiceMock).createRevision(same(noteEntity));
			assertThat(revisionInformation).isNotNull();
			assertThat(revisionInformation.getNote()).isEqualTo(note);
			assertThat(revisionInformation.isNewRevisionCreated()).isTrue();
			assertThat(revisionInformation.getCurrentRevision()).isEqualTo(currentRevision);
		}
	}

	@Test
	void updateNoteNoRevisionCreated() {

		final var id = UUID.randomUUID().toString();
		final var updateNoteRequestMock = Mockito.mock(UpdateNoteRequest.class);
		final var noteEntityMock = Mockito.mock(NoteEntity.class);
		final var noteMock = Mockito.mock(Note.class);

		// Mock
		when(noteRepositoryMock.findById(id)).thenReturn(Optional.of(noteEntityMock));
		when(revisionServiceMock.createRevision(same(noteEntityMock))).thenReturn(null);


		try (MockedStatic<NoteMapper> mapperMock = Mockito.mockStatic(NoteMapper.class)) {
			mapperMock.when(() -> NoteMapper.toNoteEntity(any(NoteEntity.class), any(UpdateNoteRequest.class))).thenReturn(noteEntityMock);
			mapperMock.when(() -> NoteMapper.toNote(any(NoteEntity.class))).thenReturn(noteMock);

			// Call
			final var revisionInformation = noteService.updateNote(id, updateNoteRequestMock);

			// Verification
			verify(noteRepositoryMock).findById(id);
			verify(noteRepositoryMock).flush();
			verify(revisionServiceMock).createRevision(same(noteEntityMock));
			verifyNoMoreInteractions(revisionServiceMock);
			mapperMock.verify(() -> NoteMapper.toNoteEntity(same(noteEntityMock), same(updateNoteRequestMock)));
			mapperMock.verify(() -> NoteMapper.toNote(same(noteEntityMock)));

			assertThat(revisionInformation).isNotNull();
			assertThat(revisionInformation.isNewRevisionCreated()).isFalse();
		}
	}

	@Test
	void updateNote() {

		final var id = UUID.randomUUID().toString();
		final var revisionId = UUID.randomUUID().toString();
		final var previousRevisionId = UUID.randomUUID().toString();
		final var updateNoteRequestMock = Mockito.mock(UpdateNoteRequest.class);
		final var noteEntityMock = Mockito.mock(NoteEntity.class);
		final var noteMock = Mockito.mock(Note.class);

		final var currentRevision = Revision.create().withId(revisionId).withVersion(2);
		final var previousRevision = Revision.create().withId(previousRevisionId).withVersion(1);
		final var oldRevision = Revision.create().withId(previousRevisionId).withVersion(0);

		// Mock
		when(noteRepositoryMock.findById(id)).thenReturn(Optional.of(noteEntityMock));
		when(revisionServiceMock.createRevision(same(noteEntityMock))).thenReturn(currentRevision);
		when(noteEntityMock.getId()).thenReturn(id);
		when(revisionServiceMock.getRevisions(id)).thenReturn(List.of(currentRevision, previousRevision, oldRevision));

		try (MockedStatic<NoteMapper> mapperMock = Mockito.mockStatic(NoteMapper.class)) {
			mapperMock.when(() -> NoteMapper.toNoteEntity(any(NoteEntity.class), any(UpdateNoteRequest.class))).thenReturn(noteEntityMock);
			mapperMock.when(() -> NoteMapper.toNote(any(NoteEntity.class))).thenReturn(noteMock);

			// Call
			final var revisionInformation = noteService.updateNote(id, updateNoteRequestMock);

			// Verification
			verify(noteRepositoryMock).findById(id);
			verify(noteRepositoryMock).flush();
			verify(revisionServiceMock).createRevision(same(noteEntityMock));
			mapperMock.verify(() -> NoteMapper.toNoteEntity(same(noteEntityMock), same(updateNoteRequestMock)));
			mapperMock.verify(() -> NoteMapper.toNote(same(noteEntityMock)));

			assertThat(revisionInformation).isNotNull();
			assertThat(revisionInformation.isNewRevisionCreated()).isTrue();
			assertThat(revisionInformation.getCurrentRevision()).isEqualTo(currentRevision);
			assertThat(revisionInformation.getPreviousRevision()).isEqualTo(previousRevision);
		}
	}

	@Test
	void updateNoteIdNotFound() {

		// Setup
		final var id = UUID.randomUUID().toString();
		final var request = UpdateNoteRequest.create();

		// Mock
		when(noteRepositoryMock.findById(id)).thenReturn(Optional.empty());

		// Call
		final var problem = assertThrows(ThrowableProblem.class, () -> noteService.updateNote(id, request));

		// Verification
		assertThat(problem).isNotNull();
		assertThat(problem.getTitle()).isEqualTo(Status.NOT_FOUND.getReasonPhrase());
		assertThat(problem.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(problem.getDetail()).isEqualTo(format(ERROR_NOTE_NOT_FOUND, id));
		verify(noteRepositoryMock).findById(id);
		verifyNoInteractions(revisionServiceMock);
	}

	@Test
	void deleteNote() {

		// Setup
		final var id = UUID.randomUUID().toString();
		final var revisionId = UUID.randomUUID().toString();
		final var currentRevision = Revision.create().withId(revisionId).withVersion(1);

		// Mock
		when(noteRepositoryMock.existsById(id)).thenReturn(true);
		when(revisionServiceMock.getRevisions(id)).thenReturn(List.of(currentRevision));

		// Call
		final var revisionInformation = noteService.deleteNoteById(id);

		// Verification
		verify(noteRepositoryMock).existsById(id);
		verify(noteRepositoryMock).deleteById(id);
		verifyNoMoreInteractions(noteRepositoryMock);
		assertThat(revisionInformation).isNotNull();
		assertThat(revisionInformation.isNewRevisionCreated()).isFalse();
		assertThat(revisionInformation.getCurrentRevision()).isEqualTo(currentRevision);
	}

	@Test
	void deleteNoteNotFound() {

		// Setup
		final var id = UUID.randomUUID().toString();

		// Call
		final var problem = assertThrows(ThrowableProblem.class, () -> noteService.deleteNoteById(id));

		// Verification
		assertThat(problem).isNotNull();
		assertThat(problem.getTitle()).isEqualTo(Status.NOT_FOUND.getReasonPhrase());
		assertThat(problem.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(problem.getDetail()).isEqualTo(format(ERROR_NOTE_NOT_FOUND, id));
		verifyNoInteractions(revisionServiceMock);
	}

	@Test
	void getNote() {

		final var id = UUID.randomUUID().toString();
		final var noteEntityMock = Mockito.mock(NoteEntity.class);
		final var noteMock = Mockito.mock(Note.class);

		// Mock
		when(noteRepositoryMock.findById(id)).thenReturn(Optional.of(noteEntityMock));

		try (MockedStatic<NoteMapper> mapperMock = Mockito.mockStatic(NoteMapper.class)) {
			mapperMock.when(() -> NoteMapper.toNote(any())).thenReturn(noteMock);

			// Call
			final var result = noteService.getNoteById(id);

			// Verification
			verify(noteRepositoryMock).findById(id);
			mapperMock.verify(() -> NoteMapper.toNote(same(noteEntityMock)));

			assertThat(result).isSameAs(noteMock);
			verifyNoInteractions(revisionServiceMock);
		}
	}

	@Test
	void getNoteNotFound() {

		// Setup
		final var id = UUID.randomUUID().toString();

		// Mock
		when(noteRepositoryMock.findById(id)).thenReturn(Optional.empty());

		// Call
		final var problem = assertThrows(ThrowableProblem.class, () -> noteService.getNoteById(id));

		// Verification
		assertThat(problem).isNotNull();
		assertThat(problem.getTitle()).isEqualTo(Status.NOT_FOUND.getReasonPhrase());
		assertThat(problem.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(problem.getDetail()).isEqualTo(format(ERROR_NOTE_NOT_FOUND, id));
		verify(noteRepositoryMock).findById(id);
		verifyNoInteractions(revisionServiceMock);
	}

	@Test
	void getNotesByPartyId() {

		// Setup
		final var id = UUID.randomUUID().toString();
		final var partyId = UUID.randomUUID().toString();
		final var findNotesRequest = FindNotesRequest.create().withPartyId(partyId).withPage(1).withLimit(100).withMunicipalityId("municipalityId");

		// Mock
		when(noteRepositoryMock.findAllByParameters(findNotesRequest, PageRequest.of(findNotesRequest.getPage() - 1, findNotesRequest.getLimit(), Sort.by("created").descending()))).thenReturn(new PageImpl<>(List.of(NoteEntity.create().withId(id)
			.withPartyId(partyId))));

		// Call
		final var result = noteService.getNotes(findNotesRequest);

		// Verification
		assertThat(result).isNotNull();
		assertThat(result.getNotes()).extracting(
				Note::getId,
				Note::getPartyId)
			.containsExactly(tuple(id, partyId));

		verify(noteRepositoryMock).findAllByParameters(any(), any());
		verifyNoInteractions(revisionServiceMock);
	}
}
