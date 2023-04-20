package se.sundsvall.notes.service;

import static java.time.OffsetDateTime.now;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import se.sundsvall.notes.integration.db.RevisionRepository;
import se.sundsvall.notes.integration.db.model.NoteEntity;
import se.sundsvall.notes.integration.db.model.RevisionEntity;

@ExtendWith(MockitoExtension.class)
class RevisionServiceTest {

	@Mock
	private RevisionRepository revisionRepositoryMock;

	@InjectMocks
	private RevisionService revisionService;

	@Captor
	private ArgumentCaptor<RevisionEntity> revisionEntityCaptor;

	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
	}

	@Test
	void createRevision() throws JsonProcessingException {

		// Setup
		final var noteEntity = createNoteEntity();
		final var revisionEntityId = UUID.randomUUID().toString();
		final var lastRevisionVersion = 3;

		when(revisionRepositoryMock.save(any())).thenReturn(RevisionEntity.create().withId(revisionEntityId));
		when(revisionRepositoryMock.findFirstByEntityIdOrderByVersionDesc(noteEntity.getId()))
			.thenReturn(Optional.of(RevisionEntity.create().withVersion(lastRevisionVersion)));

		// Call
		final var result = revisionService.createRevision(noteEntity);

		// Verification
		assertThat(result).isEqualTo(revisionEntityId);
		verify(revisionRepositoryMock).findFirstByEntityIdOrderByVersionDesc(noteEntity.getId());
		verify(revisionRepositoryMock).save(revisionEntityCaptor.capture());

		final var capturedRevisionEntity = revisionEntityCaptor.getValue();
		assertThat(capturedRevisionEntity).isNotNull();
		assertThat(capturedRevisionEntity.getVersion()).isEqualTo(lastRevisionVersion + 1);
		assertThat(capturedRevisionEntity.getSerializedSnapshot()).isEqualTo(objectMapper.writeValueAsString(noteEntity));
	}

	@Test
	void createRevisionNoPreviousRevisionExist() {

		// Setup
		final var noteEntity = createNoteEntity();
		final var revisionEntityId = UUID.randomUUID().toString();

		when(revisionRepositoryMock.save(any())).thenReturn(RevisionEntity.create().withId(revisionEntityId));
		when(revisionRepositoryMock.findFirstByEntityIdOrderByVersionDesc(noteEntity.getId())).thenReturn(empty());

		// Call
		final var result = revisionService.createRevision(noteEntity);

		// Verification
		assertThat(result).isEqualTo(revisionEntityId);
		verify(revisionRepositoryMock).findFirstByEntityIdOrderByVersionDesc(noteEntity.getId());
		verify(revisionRepositoryMock).save(revisionEntityCaptor.capture());

		final var capturedRevisionEntity = revisionEntityCaptor.getValue();
		assertThat(capturedRevisionEntity).isNotNull();
		assertThat(capturedRevisionEntity.getVersion()).isZero();
	}

	@Test
	void createRevisionNoChangeFromPreviousVersionDetected() throws JsonProcessingException {

		// Setup
		final var noteEntity = createNoteEntity();
		final var serializedSnapshot = objectMapper.writeValueAsString(noteEntity);

		when(revisionRepositoryMock.findFirstByEntityIdOrderByVersionDesc(noteEntity.getId()))
			.thenReturn(Optional.of(RevisionEntity.create().withSerializedSnapshot(serializedSnapshot)));

		// Call
		final var result = revisionService.createRevision(noteEntity);

		// Verification
		assertThat(result).isNull();
		verify(revisionRepositoryMock).findFirstByEntityIdOrderByVersionDesc(noteEntity.getId());
		verify(revisionRepositoryMock, never()).save(any());
	}

	@Test
	void createRevisionSnapshotIsInvalidJson() throws JsonProcessingException {

		// Setup
		final var noteEntity = createNoteEntity();
		final var revisionEntityId = UUID.randomUUID().toString();
		final var lastRevisionVersion = 3;
		final var invalidJson = "hello";

		when(revisionRepositoryMock.save(any())).thenReturn(RevisionEntity.create().withId(revisionEntityId));
		when(revisionRepositoryMock.findFirstByEntityIdOrderByVersionDesc(noteEntity.getId()))
			.thenReturn(Optional.of(RevisionEntity.create().withVersion(lastRevisionVersion).withSerializedSnapshot(invalidJson)));

		// Call
		final var result = revisionService.createRevision(noteEntity);

		// Verification
		assertThat(result).isEqualTo(revisionEntityId);
		verify(revisionRepositoryMock).findFirstByEntityIdOrderByVersionDesc(noteEntity.getId());
		verify(revisionRepositoryMock).save(revisionEntityCaptor.capture());

		final var capturedRevisionEntity = revisionEntityCaptor.getValue();
		assertThat(capturedRevisionEntity).isNotNull();
		assertThat(capturedRevisionEntity.getVersion()).isEqualTo(lastRevisionVersion + 1);
		assertThat(capturedRevisionEntity.getSerializedSnapshot()).isEqualTo(objectMapper.writeValueAsString(noteEntity));
	}

	private NoteEntity createNoteEntity() {
		return NoteEntity.create()
			.withBody("body")
			.withCaseId("caseId")
			.withCaseLink("caseLink")
			.withCaseType("caseType")
			.withClientId("clientId")
			.withContext("context")
			.withCreated(now())
			.withCreatedBy("createdBy")
			.withExternalCaseId("externalCaseId")
			.withId("id")
			.withModified(now())
			.withModifiedBy("modifiedBy")
			.withMunicipalityId("municipalityId")
			.withPartyId("partyId")
			.withRole("role")
			.withSubject("subject");
	}
}
