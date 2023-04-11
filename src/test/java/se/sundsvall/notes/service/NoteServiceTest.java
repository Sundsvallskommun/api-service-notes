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
import se.sundsvall.notes.api.model.UpdateNoteRequest;
import se.sundsvall.notes.integration.db.NoteRepository;
import se.sundsvall.notes.integration.db.model.NoteEntity;
import se.sundsvall.notes.service.mapper.NoteMapper;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.notes.service.ServiceConstants.ERROR_NOTE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

	@Mock
	private NoteRepository noteRepositoryMock;

	@InjectMocks
	private NoteService noteService;

	@Test
	void createNote() {
		final var id = UUID.randomUUID().toString();
		final var createNoteRequestMock = Mockito.mock(CreateNoteRequest.class);
		final var noteEntityMock = Mockito.mock(NoteEntity.class);
		final var municipalityId = "municipalityId";

		// Mock
		when(noteEntityMock.getId()).thenReturn(id);
		when(noteRepositoryMock.save(any())).thenReturn(noteEntityMock);

		try (MockedStatic<NoteMapper> mapperMock = Mockito.mockStatic(NoteMapper.class)) {
			mapperMock.when(() -> NoteMapper.toNoteEntity(anyString(), any())).thenReturn(noteEntityMock);

			// Call
			final var result = noteService.createNote(municipalityId, createNoteRequestMock);

			// Verification
			mapperMock.verify(() -> NoteMapper.toNoteEntity(eq(municipalityId), same(createNoteRequestMock)));
			verify(noteRepositoryMock).save(same(noteEntityMock));
			verify(noteEntityMock).getId();
			assertThat(result).isEqualTo(id);
		}
	}

	@Test
	void updateNote() {

		final var id = UUID.randomUUID().toString();
		final var updateNoteRequestMock = Mockito.mock(UpdateNoteRequest.class);
		final var noteEntityMock = Mockito.mock(NoteEntity.class);
		final var noteMock = Mockito.mock(Note.class);
		final var municipalityId = "municipalityId";

		// Mock
		when(noteRepositoryMock.existsByIdAndMunicipalityId(id, municipalityId)).thenReturn(true);
		when(noteRepositoryMock.getReferenceById(id)).thenReturn(noteEntityMock);
		when(noteRepositoryMock.save(any())).thenReturn(noteEntityMock);

		try (MockedStatic<NoteMapper> mapperMock = Mockito.mockStatic(NoteMapper.class)) {
			mapperMock.when(() -> NoteMapper.toNoteEntity(anyString(), any(), any())).thenReturn(noteEntityMock);
			mapperMock.when(() -> NoteMapper.toNote(any())).thenReturn(noteMock);

			// Call
			final var result = noteService.updateNote(id, municipalityId, updateNoteRequestMock);

			// Verification
			verify(noteRepositoryMock).existsByIdAndMunicipalityId(id, municipalityId);
			verify(noteRepositoryMock).save(same(noteEntityMock));
			verify(noteRepositoryMock).getReferenceById(id);
			mapperMock.verify(() -> NoteMapper.toNoteEntity(eq(municipalityId), same(noteEntityMock), same(updateNoteRequestMock)));
			mapperMock.verify(() -> NoteMapper.toNote(same(noteEntityMock)));

			assertThat(result).isSameAs(noteMock);
		}
	}

	@Test
	void updateNoteIdNotFound() {

		// Setup
		final var id = UUID.randomUUID().toString();
		final var request = UpdateNoteRequest.create();
		final var municipalityId = "municipalityId";

		// Mock
		when(noteRepositoryMock.existsByIdAndMunicipalityId(id, municipalityId)).thenReturn(false);

		// Call
		final var problem = assertThrows(ThrowableProblem.class, () -> noteService.updateNote(id, municipalityId, request));

		// Verification
		assertThat(problem).isNotNull();
		assertThat(problem.getTitle()).isEqualTo(Status.NOT_FOUND.getReasonPhrase());
		assertThat(problem.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(problem.getDetail()).isEqualTo(format(ERROR_NOTE_NOT_FOUND, id));
		verify(noteRepositoryMock).existsByIdAndMunicipalityId(id, municipalityId);
	}

	@Test
	void deleteNote() {

		// Setup
		final var id = UUID.randomUUID().toString();
		final var municipalityId = "2281";

		// Mock
		when(noteRepositoryMock.existsByIdAndMunicipalityId(id, municipalityId)).thenReturn(true);

		// Call
		noteService.deleteNote(id, municipalityId);

		// Verification
		verify(noteRepositoryMock).existsByIdAndMunicipalityId(id, municipalityId);
		verify(noteRepositoryMock).deleteById(id);
	}

	@Test
	void deleteNoteNotFound() {

		// Setup
		final var id = UUID.randomUUID().toString();
		final var municipalityId = "2281";

		// Call
		final var problem = assertThrows(ThrowableProblem.class, () -> noteService.deleteNote(id, municipalityId));

		// Verification
		assertThat(problem).isNotNull();
		assertThat(problem.getTitle()).isEqualTo(Status.NOT_FOUND.getReasonPhrase());
		assertThat(problem.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(problem.getDetail()).isEqualTo(format(ERROR_NOTE_NOT_FOUND, id));
		verify(noteRepositoryMock).existsByIdAndMunicipalityId(id, municipalityId);
	}

	@Test
	void getNote() {

		final var id = UUID.randomUUID().toString();
		final var municipalityId = "municipalityId";
		final var noteEntityMock = Mockito.mock(NoteEntity.class);
		final var noteMock = Mockito.mock(Note.class);

		// Mock
		when(noteRepositoryMock.existsByIdAndMunicipalityId(id, municipalityId)).thenReturn(true);
		when(noteRepositoryMock.getReferenceById(id)).thenReturn(noteEntityMock);

		try (MockedStatic<NoteMapper> mapperMock = Mockito.mockStatic(NoteMapper.class)) {
			mapperMock.when(() -> NoteMapper.toNote(any())).thenReturn(noteMock);

			// Call
			final var result = noteService.getNote(id, municipalityId);

			// Verification
			verify(noteRepositoryMock).existsByIdAndMunicipalityId(id, municipalityId);
			mapperMock.verify(() -> NoteMapper.toNote(same(noteEntityMock)));

			assertThat(result).isSameAs(noteMock);
		}
	}

	@Test
	void getNoteNotFound() {

		// Setup
		final var id = UUID.randomUUID().toString();
		final var municipalityId = "municipalityId";

		// Mock
		when(noteRepositoryMock.existsByIdAndMunicipalityId(id, municipalityId)).thenReturn(false);

		// Call
		final var problem = assertThrows(ThrowableProblem.class, () -> noteService.getNote(id, municipalityId));

		// Verification
		assertThat(problem).isNotNull();
		assertThat(problem.getTitle()).isEqualTo(Status.NOT_FOUND.getReasonPhrase());
		assertThat(problem.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(problem.getDetail()).isEqualTo(format(ERROR_NOTE_NOT_FOUND, id));
		verify(noteRepositoryMock).existsByIdAndMunicipalityId(id, municipalityId);
	}

	@Test
	void getNotesByPartyId() {

		// Setup
		final var id = UUID.randomUUID().toString();
		final var partyId = UUID.randomUUID().toString();
		final var findNotesRequest = FindNotesRequest.create().withPartyId(partyId).withPage(1).withLimit(100);
		final var municipalityId = "municipalityId";

		// Mock
		when(noteRepositoryMock.findAllByParameters(municipalityId, findNotesRequest, PageRequest.of(findNotesRequest.getPage() - 1, findNotesRequest.getLimit(), Sort.by("created").descending()))).thenReturn(new PageImpl<>(List.of(NoteEntity.create().withId(id)
			.withPartyId(partyId))));

		// Call
		final var result = noteService.getNotes(municipalityId, findNotesRequest);

		// Verification
		assertThat(result).isNotNull();
		assertThat(result.getNotes()).extracting(
			Note::getId,
			Note::getPartyId)
			.containsExactly(tuple(id, partyId));

		verify(noteRepositoryMock).findAllByParameters(anyString(), any(), any());
	}
}
