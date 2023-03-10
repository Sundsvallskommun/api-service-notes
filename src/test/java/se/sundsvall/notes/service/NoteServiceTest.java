package se.sundsvall.notes.service;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.notes.service.ServiceConstants.ERROR_NOTE_NOT_FOUND;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

		// Mock
		when(noteEntityMock.getId()).thenReturn(id);
		when(noteRepositoryMock.save(any())).thenReturn(noteEntityMock);

		try (MockedStatic<NoteMapper> mapperMock = Mockito.mockStatic(NoteMapper.class)) {
			mapperMock.when(() -> NoteMapper.toNoteEntity(any())).thenReturn(noteEntityMock);

			// Call
			final var result = noteService.createNote(createNoteRequestMock);

			// Verification
			mapperMock.verify(() -> NoteMapper.toNoteEntity(same(createNoteRequestMock)));
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

		// Mock
		when(noteRepositoryMock.findById(id)).thenReturn(Optional.of(noteEntityMock));

		try (MockedStatic<NoteMapper> mapperMock = Mockito.mockStatic(NoteMapper.class)) {
			mapperMock.when(() -> NoteMapper.toNoteEntity(any(), any())).thenReturn(noteEntityMock);
			mapperMock.when(() -> NoteMapper.toNote(any())).thenReturn(noteMock);

			// Call
			final var result = noteService.updateNote(id, updateNoteRequestMock);

			// Verification
			verify(noteRepositoryMock).save(same(noteEntityMock));
			verify(noteRepositoryMock).findById(id);
			mapperMock.verify(() -> NoteMapper.toNoteEntity(same(noteEntityMock), same(updateNoteRequestMock)));
			mapperMock.verify(() -> NoteMapper.toNote(same(noteEntityMock)));

			assertThat(result).isSameAs(noteMock);
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
	}

	@Test
	void deleteNoteById() {

		// Setup
		final var id = UUID.randomUUID().toString();

		// Mock
		when(noteRepositoryMock.existsById(id)).thenReturn(true);

		// Call
		noteService.deleteNoteById(id);

		// Verification
		verify(noteRepositoryMock).existsById(id);
		verify(noteRepositoryMock).deleteById(id);
	}

	@Test
	void deleteNoteByIdNotFound() {

		// Setup
		final var id = UUID.randomUUID().toString();

		// Call
		final var problem = assertThrows(ThrowableProblem.class, () -> noteService.deleteNoteById(id));

		// Verification
		assertThat(problem).isNotNull();
		assertThat(problem.getTitle()).isEqualTo(Status.NOT_FOUND.getReasonPhrase());
		assertThat(problem.getStatus()).isEqualTo(Status.NOT_FOUND);
		assertThat(problem.getDetail()).isEqualTo(format(ERROR_NOTE_NOT_FOUND, id));
		verify(noteRepositoryMock).existsById(id);
	}

	@Test
	void getNoteById() {

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
		}
	}

	@Test
	void getNoteByIdNotFound() {

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
	}

	@Test
	void getNotesByPartyId() {

		// Setup
		final var id = UUID.randomUUID().toString();
		final var partyId = UUID.randomUUID().toString();
		final var findNotesRequest = FindNotesRequest.create().withPartyId(partyId).withPage(1).withLimit(100);

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
	}
}
