package se.sundsvall.notes.service;

import static java.time.OffsetDateTime.now;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.notes.api.model.Operation;
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.integration.db.RevisionRepository;
import se.sundsvall.notes.integration.db.model.NoteEntity;
import se.sundsvall.notes.integration.db.model.RevisionEntity;

@ExtendWith(MockitoExtension.class)
class RevisionServiceTest {

	@Mock
	private RevisionRepository revisionRepositoryMock;

	@Spy
	private ObjectMapper objectMapperSpy;

	@InjectMocks
	private RevisionService revisionService;

	@Captor
	private ArgumentCaptor<RevisionEntity> revisionEntityCaptor;

	@BeforeEach
	void setup() {
		objectMapperSpy.registerModule(new JavaTimeModule());
	}

	@Test
	void createRevision() throws JsonProcessingException {

		// Arrange
		final var noteEntity = createNoteEntity();
		final var revisionEntityId = UUID.randomUUID().toString();
		final var lastRevisionVersion = 3;
		final var serializedSnapshot = objectMapperSpy.writeValueAsString(noteEntity);
		final var currentRevisionEntity = RevisionEntity.create().withEntityId(noteEntity.getId()).withId(revisionEntityId).withVersion(lastRevisionVersion + 1);
		final var expectedRevision = Revision.create().withId(revisionEntityId).withVersion(lastRevisionVersion + 1).withEntityId(noteEntity.getId());
		final var municipalityId = "municipalityId";

		when(revisionRepositoryMock.save(any())).thenReturn(currentRevisionEntity);
		when(revisionRepositoryMock.findFirstByEntityIdAndMunicipalityIdOrderByVersionDesc(noteEntity.getId(), municipalityId))
			.thenReturn(Optional.of(RevisionEntity.create().withVersion(lastRevisionVersion)));

		// Act
		final var createdRevision = revisionService.createRevision(noteEntity, municipalityId);

		// Assert
		assertThat(createdRevision).isNotNull().isEqualTo(expectedRevision);
		verify(revisionRepositoryMock).findFirstByEntityIdAndMunicipalityIdOrderByVersionDesc(noteEntity.getId(), municipalityId);
		verify(revisionRepositoryMock).save(revisionEntityCaptor.capture());

		final var capturedRevisionEntity = revisionEntityCaptor.getValue();
		assertThat(capturedRevisionEntity).isNotNull();
		assertThat(capturedRevisionEntity.getVersion()).isEqualTo(lastRevisionVersion + 1);
		assertThat(capturedRevisionEntity.getSerializedSnapshot()).isEqualTo(serializedSnapshot);
	}

	@Test
	void createRevisionNoPreviousRevisionExist() {

		// Arrange
		final var noteEntity = createNoteEntity();
		final var revisionEntityId = UUID.randomUUID().toString();
		final var revisionEntity = RevisionEntity.create().withEntityId(noteEntity.getId()).withId(revisionEntityId).withVersion(0);
		final var expectedRevision = Revision.create().withId(revisionEntityId).withEntityId(noteEntity.getId()).withVersion(0);
		final var municipalityId = "municipalityId";

		when(revisionRepositoryMock.save(any())).thenReturn(revisionEntity);
		when(revisionRepositoryMock.findFirstByEntityIdAndMunicipalityIdOrderByVersionDesc(noteEntity.getId(), municipalityId)).thenReturn(empty());

		// Act
		final var createdRevision = revisionService.createRevision(noteEntity, municipalityId);

		// Assert
		assertThat(createdRevision).isNotNull().isEqualTo(expectedRevision);
		verify(revisionRepositoryMock).findFirstByEntityIdAndMunicipalityIdOrderByVersionDesc(noteEntity.getId(), municipalityId);
		verify(revisionRepositoryMock).save(revisionEntityCaptor.capture());

		final var capturedRevisionEntity = revisionEntityCaptor.getValue();
		assertThat(capturedRevisionEntity).isNotNull();
		assertThat(capturedRevisionEntity.getVersion()).isZero();
	}

	@Test
	void createRevisionNoChangeFromPreviousVersionDetected() throws JsonProcessingException {

		// Arrange
		final var noteEntity = createNoteEntity();
		final var serializedSnapshot = objectMapperSpy.writeValueAsString(noteEntity);
		final var municipalityId = "municipalityId";

		when(revisionRepositoryMock.findFirstByEntityIdAndMunicipalityIdOrderByVersionDesc(noteEntity.getId(), municipalityId))
			.thenReturn(Optional.of(RevisionEntity.create().withSerializedSnapshot(serializedSnapshot)));

		// Act
		final var createdRevision = revisionService.createRevision(noteEntity, municipalityId);

		// Assert
		assertThat(createdRevision).isNull();
		verify(revisionRepositoryMock).findFirstByEntityIdAndMunicipalityIdOrderByVersionDesc(noteEntity.getId(), municipalityId);
		verify(revisionRepositoryMock, never()).save(any());

	}

	@Test
	void createRevisionSnapshotIsInvalidJson() throws JsonProcessingException {

		// Arrange
		final var noteEntity = createNoteEntity();
		final var revisionEntityId = UUID.randomUUID().toString();
		final var lastRevisionVersion = 3;
		final var invalidJson = "hello";
		final var serializedSnapshot = objectMapperSpy.writeValueAsString(noteEntity);
		final var currentRevisionEntity = RevisionEntity.create().withEntityId(noteEntity.getId()).withId(revisionEntityId).withVersion(lastRevisionVersion + 1);
		final var expectedRevision = Revision.create().withId(revisionEntityId).withVersion(lastRevisionVersion + 1).withEntityId(noteEntity.getId());
		final var municipalityId = "municipalityId";

		when(revisionRepositoryMock.save(any())).thenReturn(currentRevisionEntity);
		when(revisionRepositoryMock.findFirstByEntityIdAndMunicipalityIdOrderByVersionDesc(noteEntity.getId(), municipalityId))
			.thenReturn(Optional.of(RevisionEntity.create().withVersion(lastRevisionVersion).withSerializedSnapshot(invalidJson)));

		// Act
		final var createdRevision = revisionService.createRevision(noteEntity, municipalityId);

		// Assert
		assertThat(createdRevision).isNotNull().isEqualTo(expectedRevision);
		verify(revisionRepositoryMock).findFirstByEntityIdAndMunicipalityIdOrderByVersionDesc(noteEntity.getId(), municipalityId);
		verify(revisionRepositoryMock).save(revisionEntityCaptor.capture());

		final var capturedRevisionEntity = revisionEntityCaptor.getValue();
		assertThat(capturedRevisionEntity).isNotNull();
		assertThat(capturedRevisionEntity.getVersion()).isEqualTo(lastRevisionVersion + 1);
		assertThat(capturedRevisionEntity.getSerializedSnapshot()).isEqualTo(serializedSnapshot);
	}

	@Test
	void getRevisions() {

		// Arrange
		final var entityId = UUID.randomUUID().toString();
		final var municipalityId = "municipalityId";

		when(revisionRepositoryMock.findAllByEntityIdAndMunicipalityIdOrderByVersionDesc(anyString(), anyString())).thenReturn(List.of(RevisionEntity.create().withEntityId(entityId)));

		// Act
		final var result = revisionService.getRevisions(entityId, municipalityId);

		// Assert
		assertThat(result).isNotNull();
		verify(revisionRepositoryMock).findAllByEntityIdAndMunicipalityIdOrderByVersionDesc(entityId, municipalityId);
	}

	@Test
	void diff() throws JsonProcessingException {

		// Arrange
		final var entityId = UUID.randomUUID().toString();
		final var source = 1;
		final var target = 2;
		final var noteEntity1 = createNoteEntity();
		final var noteEntity2 = SerializationUtils.clone(noteEntity1).withBody("changed body").withModifiedBy("user22");
		final var municipalityId = "municipalityId";

		when(revisionRepositoryMock.findByEntityIdAndMunicipalityIdAndVersion(entityId, municipalityId, source)).thenReturn(Optional.of(RevisionEntity.create()
			.withEntityId(entityId)
			.withVersion(source)
			.withSerializedSnapshot(toJsonString(noteEntity1))));

		when(revisionRepositoryMock.findByEntityIdAndMunicipalityIdAndVersion(entityId, municipalityId, target)).thenReturn(Optional.of(RevisionEntity.create()
			.withEntityId(entityId)
			.withVersion(target)
			.withSerializedSnapshot(toJsonString(noteEntity2))));

		// Act
		final var result = revisionService.diff(entityId, municipalityId, source, target);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getOperations())
			.hasSize(2)
			.extracting(
				Operation::getOp,
				Operation::getPath,
				Operation::getValue,
				Operation::getFromValue)
			.containsExactly(
				tuple("replace", "/modifiedBy", "user22", null),
				tuple("replace", "/body", "changed body", "body"));

		verify(revisionRepositoryMock).findByEntityIdAndMunicipalityIdAndVersion(entityId, municipalityId, source);
		verify(revisionRepositoryMock).findByEntityIdAndMunicipalityIdAndVersion(entityId, municipalityId, target);
	}

	@Test
	void diffWhenErrorOccur() {

		// Arrange
		final var entityId = UUID.randomUUID().toString();
		final var source = 1;
		final var target = 2;
		final var municipalityId = "municipalityId";

		when(revisionRepositoryMock.findByEntityIdAndMunicipalityIdAndVersion(entityId, municipalityId, source)).thenReturn(Optional.of(RevisionEntity.create()
			.withEntityId(entityId)
			.withMunicipalityId(municipalityId)
			.withVersion(source)
			.withSerializedSnapshot("{}")));

		when(revisionRepositoryMock.findByEntityIdAndMunicipalityIdAndVersion(entityId, municipalityId, target)).thenReturn(Optional.of(RevisionEntity.create()
			.withEntityId(entityId)
			.withMunicipalityId(municipalityId)
			.withVersion(target)
			.withSerializedSnapshot("{"))); // Faulty json

		// Act
		final var problem = assertThrows(ThrowableProblem.class, () -> revisionService.diff(entityId, municipalityId, source, target));

		// Assert
		assertThat(problem).isNotNull();
		assertThat(problem.getMessage()).isEqualTo(String.format("Internal Server Error: An error occurred during diff of entityId '%s' looking at version '%s' and version '%s'!", entityId, source, target));
		assertThat(problem.getStatus()).isEqualTo(INTERNAL_SERVER_ERROR);

		verify(revisionRepositoryMock).findByEntityIdAndMunicipalityIdAndVersion(entityId, municipalityId, source);
		verify(revisionRepositoryMock).findByEntityIdAndMunicipalityIdAndVersion(entityId, municipalityId, target);
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
			.withModifiedBy(null)
			.withMunicipalityId("municipalityId")
			.withPartyId("partyId")
			.withRole("role")
			.withSubject("subject");
	}

	private String toJsonString(final NoteEntity entity) throws JsonProcessingException {
		return new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.writeValueAsString(entity);
	}
}
