package se.sundsvall.notes.service;

import generated.se.sundsvall.eventlog.Event;
import generated.se.sundsvall.eventlog.EventType;
import generated.se.sundsvall.eventlog.Metadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import se.sundsvall.notes.api.filter.ExecutingUserSupplier;
import se.sundsvall.notes.api.model.CreateNoteRequest;
import se.sundsvall.notes.api.model.FindNotesRequest;
import se.sundsvall.notes.api.model.Note;
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.api.model.UpdateNoteRequest;
import se.sundsvall.notes.integration.db.NoteRepository;
import se.sundsvall.notes.integration.db.model.NoteEntity;
import se.sundsvall.notes.integration.eventlog.EventlogClient;
import se.sundsvall.notes.service.mapper.NoteMapper;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.notes.service.ServiceConstants.ERROR_NOTE_NOT_FOUND;
import static se.sundsvall.notes.service.ServiceConstants.EVENT_LOG_CREATE_NOTE;
import static se.sundsvall.notes.service.ServiceConstants.EVENT_LOG_DELETE_NOTE;
import static se.sundsvall.notes.service.ServiceConstants.EVENT_LOG_UPDATE_NOTE;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

	private final static String EXECUTED_BY = "ExecutedBy";
	private final static String CREATED_BY = "CreatedBy";
	private final static String MODIFIED_BY = "ModifiedBy";
	private final static String USER_ID = "userId";
	private final static String USER_ID_2 = "userId-2";
	private final static String CURRENT_VERSION = "CurrentVersion";
	private final static String CURRENT_REVISION = "CurrentRevision";
	private final static String PREVIOUS_VERSION = "PreviousVersion";
	private final static String PREVIOUS_REVISION = "PreviousRevision";

	@Mock
	private NoteRepository noteRepositoryMock;

	@Mock
	private RevisionService revisionServiceMock;

	@Mock
	private EventlogClient eventlogClientMock;

	@Mock
	private ExecutingUserSupplier executingUserSupplierMock;

	private final ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

	@InjectMocks
	private NoteService noteService;

	@Test
	void createNote() {
		final var id = UUID.randomUUID().toString();
		final var createNoteRequestMock = Mockito.mock(CreateNoteRequest.class);
		final var noteEntityMock = Mockito.mock(NoteEntity.class);

		// Mock
		when(noteEntityMock.getId()).thenReturn(id);
		when(noteEntityMock.getCreatedBy()).thenReturn(USER_ID);
		when(noteEntityMock.getModifiedBy()).thenReturn(USER_ID_2);
		when(noteRepositoryMock.save(any())).thenReturn(noteEntityMock);
		when(executingUserSupplierMock.getAdUser()).thenReturn(USER_ID);
		when(revisionServiceMock.createNoteRevision(noteEntityMock)).thenReturn(Revision.create().withVersion(1).withId(id));
		when(noteRepositoryMock.save(any())).thenReturn(noteEntityMock);
		when(executingUserSupplierMock.getAdUser()).thenReturn(USER_ID);
		when(revisionServiceMock.createNoteRevision(noteEntityMock)).thenReturn(Revision.create().withVersion(1).withId(id));

		try (MockedStatic<NoteMapper> mapperMock = Mockito.mockStatic(NoteMapper.class)) {
			mapperMock.when(() -> NoteMapper.toNoteEntity(any())).thenReturn(noteEntityMock);

			// Call
			final var result = noteService.createNote(createNoteRequestMock);

			// Verification
			mapperMock.verify(() -> NoteMapper.toNoteEntity(same(createNoteRequestMock)));
			verify(noteRepositoryMock).save(same(noteEntityMock));
			verify(revisionServiceMock).createNoteRevision(same(noteEntityMock));
			verify(executingUserSupplierMock).getAdUser();
			verify(eventlogClientMock).createEvent(eq(id), eventCaptor.capture());
			verify(noteEntityMock, times(2)).getId();
			verify(noteEntityMock).getCreatedBy();
			verify(noteEntityMock).getModifiedBy();
			assertThat(result).isEqualTo(id);

			final var event = eventCaptor.getValue();
			assertThat(event.getType()).isEqualTo(EventType.CREATE);
			assertThat(event.getHistoryReference()).isEqualTo(id);
			assertThat(event.getCreated()).isCloseTo(OffsetDateTime.now().now(), within(1, ChronoUnit.SECONDS));
			assertThat(event.getMessage()).isEqualTo(EVENT_LOG_CREATE_NOTE);
			assertThat(event.getOwner()).isEqualTo("Notes");
			assertThat(event.getSourceType()).isEqualTo("Note");
			assertThat(event.getMetadata()).isNotNull().hasSize(5)
				.extracting(Metadata::getKey, Metadata::getValue)
				.containsExactly(
					tuple(CREATED_BY, USER_ID),
					tuple(CURRENT_VERSION, "1"),
					tuple(MODIFIED_BY, USER_ID_2),
					tuple(CURRENT_REVISION, id),
					tuple(EXECUTED_BY, USER_ID));
		}
	}

	@Test
	void createNoteNoUserInHeader() {
		final var id = UUID.randomUUID().toString();
		final var createNoteRequestMock = Mockito.mock(CreateNoteRequest.class);
		final var noteEntityMock = Mockito.mock(NoteEntity.class);

		// Mock
		when(noteEntityMock.getId()).thenReturn(id);
		when(noteEntityMock.getCreatedBy()).thenReturn(USER_ID);
		when(noteEntityMock.getModifiedBy()).thenReturn(USER_ID_2);
		when(noteRepositoryMock.save(any())).thenReturn(noteEntityMock);
		when(executingUserSupplierMock.getAdUser()).thenReturn("UNKNOWN");
		when(revisionServiceMock.createNoteRevision(noteEntityMock)).thenReturn(Revision.create().withVersion(1).withId(id));

		try (MockedStatic<NoteMapper> mapperMock = Mockito.mockStatic(NoteMapper.class)) {
			mapperMock.when(() -> NoteMapper.toNoteEntity(any())).thenReturn(noteEntityMock);

			// Call
			final var result = noteService.createNote(createNoteRequestMock);

			// Verification
			mapperMock.verify(() -> NoteMapper.toNoteEntity(same(createNoteRequestMock)));
			verify(noteRepositoryMock).save(same(noteEntityMock));
			verify(revisionServiceMock).createNoteRevision(same(noteEntityMock));
			verify(executingUserSupplierMock).getAdUser();
			verify(noteEntityMock, times(2)).getId();
			verify(eventlogClientMock).createEvent(eq(id), eventCaptor.capture());
			assertThat(result).isEqualTo(id);

			final var event = eventCaptor.getValue();
			assertThat(event.getType()).isEqualTo(EventType.CREATE);
			assertThat(event.getMetadata()).isNotNull().hasSize(5)
				.extracting(Metadata::getKey, Metadata::getValue)
				.containsExactly(
					tuple(CREATED_BY, USER_ID),
					tuple(CURRENT_VERSION, "1"),
					tuple(MODIFIED_BY, USER_ID_2),
					tuple(CURRENT_REVISION, id),
					tuple(EXECUTED_BY, "UNKNOWN"));
		}
	}

	@Test
	void updateNote() {

		final var id = UUID.randomUUID().toString();
		final var oldId = UUID.randomUUID().toString();
		final var updateNoteRequestMock = Mockito.mock(UpdateNoteRequest.class);
		final var noteEntityMock = Mockito.mock(NoteEntity.class);
		final var noteMock = Mockito.mock(Note.class);

		// Mock
		when(noteRepositoryMock.findById(id)).thenReturn(Optional.of(noteEntityMock));
		when(executingUserSupplierMock.getAdUser()).thenReturn(USER_ID);
		when(revisionServiceMock.createNoteRevision(noteEntityMock)).thenReturn(Revision.create().withVersion(1).withId(id));
		when(revisionServiceMock.getRevisions(id)).thenReturn(List.of(Revision.create().withVersion(1).withId(id), Revision.create().withVersion(0).withId(oldId)));

		try (MockedStatic<NoteMapper> mapperMock = Mockito.mockStatic(NoteMapper.class)) {
			mapperMock.when(() -> NoteMapper.toNoteEntity(any(NoteEntity.class), any(UpdateNoteRequest.class))).thenReturn(noteEntityMock);
			mapperMock.when(() -> NoteMapper.toNote(any(NoteEntity.class))).thenReturn(noteMock);
			when(noteEntityMock.getId()).thenReturn(id);
			// Call
			final var result = noteService.updateNote(id, updateNoteRequestMock);

			// Verification
			verify(noteRepositoryMock).findById(id);
			verify(noteRepositoryMock).flush();
			verify(revisionServiceMock).createNoteRevision(same(noteEntityMock));
			mapperMock.verify(() -> NoteMapper.toNoteEntity(same(noteEntityMock), same(updateNoteRequestMock)));
			mapperMock.verify(() -> NoteMapper.toNote(same(noteEntityMock)));
			verify(eventlogClientMock).createEvent(eq(id), eventCaptor.capture());

			assertThat(result).isSameAs(noteMock);

			final var event = eventCaptor.getValue();
			assertThat(event.getType()).isEqualTo(EventType.UPDATE);
			assertThat(event.getHistoryReference()).isEqualTo(id);
			assertThat(event.getCreated()).isCloseTo(OffsetDateTime.now().now(), within(1, ChronoUnit.SECONDS));
			assertThat(event.getMessage()).isEqualTo(EVENT_LOG_UPDATE_NOTE);
			assertThat(event.getOwner()).isEqualTo("Notes");
			assertThat(event.getSourceType()).isEqualTo("Note");
			assertThat(event.getMetadata()).isNotNull().hasSize(5)
				.extracting(Metadata::getKey, Metadata::getValue)
				.containsExactly(
					tuple(PREVIOUS_REVISION, oldId),
					tuple(CURRENT_VERSION, "1"),
					tuple(PREVIOUS_VERSION, "0"),
					tuple(CURRENT_REVISION, id),
					tuple(EXECUTED_BY, USER_ID));
		}
	}

	@Test
	void updateNoteNoUserInHeader() {

		final var id = UUID.randomUUID().toString();
		final var oldId = UUID.randomUUID().toString();
		final var updateNoteRequestMock = Mockito.mock(UpdateNoteRequest.class);
		final var noteEntityMock = Mockito.mock(NoteEntity.class);
		final var noteMock = Mockito.mock(Note.class);

		// Mock
		when(noteRepositoryMock.findById(id)).thenReturn(Optional.of(noteEntityMock));
		when(executingUserSupplierMock.getAdUser()).thenReturn("UNKNOWN");
		when(revisionServiceMock.createNoteRevision(noteEntityMock)).thenReturn(Revision.create().withVersion(1).withId(id));
		when(revisionServiceMock.getRevisions(id)).thenReturn(List.of(Revision.create().withVersion(1).withId(id), Revision.create().withVersion(0).withId(oldId)));

		try (MockedStatic<NoteMapper> mapperMock = Mockito.mockStatic(NoteMapper.class)) {
			mapperMock.when(() -> NoteMapper.toNoteEntity(any(NoteEntity.class), any(UpdateNoteRequest.class))).thenReturn(noteEntityMock);
			mapperMock.when(() -> NoteMapper.toNote(any(NoteEntity.class))).thenReturn(noteMock);
			when(noteEntityMock.getId()).thenReturn(id);
			when(noteEntityMock.getCreatedBy()).thenReturn(USER_ID);
			when(noteEntityMock.getModifiedBy()).thenReturn(USER_ID_2);
			// Call
			final var result = noteService.updateNote(id, updateNoteRequestMock);

			// Verification
			verify(noteRepositoryMock).findById(id);
			verify(noteRepositoryMock).flush();
			verify(revisionServiceMock).createNoteRevision(same(noteEntityMock));
			mapperMock.verify(() -> NoteMapper.toNoteEntity(same(noteEntityMock), same(updateNoteRequestMock)));
			mapperMock.verify(() -> NoteMapper.toNote(same(noteEntityMock)));
			verify(eventlogClientMock).createEvent(eq(id), eventCaptor.capture());

			assertThat(result).isSameAs(noteMock);

			final var event = eventCaptor.getValue();
			assertThat(event.getType()).isEqualTo(EventType.UPDATE);
			assertThat(event.getHistoryReference()).isEqualTo(id);
			assertThat(event.getCreated()).isCloseTo(OffsetDateTime.now().now(), within(1, ChronoUnit.SECONDS));
			assertThat(event.getMessage()).isEqualTo(EVENT_LOG_UPDATE_NOTE);
			assertThat(event.getOwner()).isEqualTo("Notes");
			assertThat(event.getSourceType()).isEqualTo("Note");
			assertThat(event.getMetadata()).isNotNull();
			assertThat(event.getMetadata()).isNotNull().hasSize(7)
				.extracting(Metadata::getKey, Metadata::getValue)
				.containsExactly(
					tuple(CREATED_BY, USER_ID),
					tuple(PREVIOUS_REVISION, oldId),
					tuple(CURRENT_VERSION, "1"),
					tuple(PREVIOUS_VERSION, "0"),
					tuple(MODIFIED_BY, USER_ID_2),
					tuple(CURRENT_REVISION, id),
					tuple(EXECUTED_BY, "UNKNOWN"));
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
		verifyNoInteractions(revisionServiceMock, eventlogClientMock);
	}

	@Test
	void deleteNote() {

		// Setup
		final var id = UUID.randomUUID().toString();
		final var revision = Revision.create().withId(id).withVersion(1);

		// Mock
		when(noteRepositoryMock.findById(id)).thenReturn(Optional.of(NoteEntity.create().withId(id)));
		when(executingUserSupplierMock.getAdUser()).thenReturn(USER_ID);
		when(revisionServiceMock.getLatestNoteRevision(id)).thenReturn(revision);

		// Call
		noteService.deleteNoteById(id);

		// Verification
		verify(noteRepositoryMock).findById(id);
		verify(noteRepositoryMock).deleteById(id);
		verify(eventlogClientMock).createEvent(eq(id), eventCaptor.capture());
		verify(revisionServiceMock).getLatestNoteRevision(id);

		final var event = eventCaptor.getValue();
		assertThat(event.getType()).isEqualTo(EventType.DELETE);
		assertThat(event.getHistoryReference()).isEqualTo(id);
		assertThat(event.getCreated()).isCloseTo(OffsetDateTime.now().now(), within(1, ChronoUnit.SECONDS));
		assertThat(event.getMessage()).isEqualTo(EVENT_LOG_DELETE_NOTE);
		assertThat(event.getOwner()).isEqualTo("Notes");
		assertThat(event.getSourceType()).isEqualTo("Note");
		assertThat(event.getMetadata()).isNotNull();
		assertThat(event.getMetadata()).hasSize(3)
			.extracting(Metadata::getKey, Metadata::getValue)
			.containsExactly(
				tuple(CURRENT_VERSION, "1"),
				tuple(CURRENT_REVISION, id),
				tuple(EXECUTED_BY, USER_ID));
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
		verify(noteRepositoryMock).findById(id);
		verifyNoInteractions(revisionServiceMock, eventlogClientMock);
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
			verifyNoInteractions(revisionServiceMock, eventlogClientMock);
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
		verifyNoInteractions(revisionServiceMock, eventlogClientMock);
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
		verifyNoInteractions(revisionServiceMock, eventlogClientMock);
	}
}
