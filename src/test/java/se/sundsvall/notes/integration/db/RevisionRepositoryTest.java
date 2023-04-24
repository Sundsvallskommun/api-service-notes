package se.sundsvall.notes.integration.db;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.Assertions.within;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.notes.Application;
import se.sundsvall.notes.integration.db.model.NoteEntity;
import se.sundsvall.notes.integration.db.model.RevisionEntity;

/**
 * Revision repository tests.
 *
 * @see src/test/resources/db/scripts/RevisionRepositoryTest.sql for data setup.
 */
@SpringBootTest(classes = { Application.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/RevisionRepositoryTest.sql"
})
@Transactional
class RevisionRepositoryTest {

	private static final String ENTITY_ID_1 = "9791682e-4ba8-4f3a-857a-54e14836a53b";
	private static final String ENTITY_ID_2 = "abe72bbd-9808-4f3a-8aec-cd2945f5a201";

	@Autowired
	private RevisionRepository repository;

	@Test
	void create() {

		final var result = repository.save(RevisionEntity.create()
			.withEntityId(randomUUID().toString())
			.withEntityType(NoteEntity.class.getSimpleName())
			.withSerializedSnapshot("{}")
			.withVersion(0));

		assertThat(result).isNotNull();
		assertThat(result.getCreated()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
		assertThat(result.getEntityType()).isEqualTo("NoteEntity");
		assertThat(isValidUUID(result.getId())).isTrue();
		assertThat(isValidUUID(result.getEntityId())).isTrue();
	}

	@Test
	void findByEntityIdAndVersion() {

		// Setup
		final var version = 3;

		final var revision = repository.findByEntityIdAndVersion(ENTITY_ID_1, version);

		assertThat(revision)
			.isNotNull()
			.get()
			.extracting(RevisionEntity::getEntityId, RevisionEntity::getVersion)
			.containsExactly(ENTITY_ID_1, 3);
	}

	@Test
	void findByEntityIdAndVersionNotFound() {

		// Setup
		final var version = 666;

		final var revision = repository.findByEntityIdAndVersion(ENTITY_ID_1, version);

		assertThat(revision).isEmpty();
	}

	@Test
	void findFirstByEntityIdOrderByVersionDesc() {

		final var revision = repository.findFirstByEntityIdOrderByVersionDesc(ENTITY_ID_1);

		assertThat(revision).isPresent();
		assertThat(revision.get().getEntityId()).isEqualTo(ENTITY_ID_1);
		assertThat(revision.get().getVersion()).isEqualTo(5);
	}

	@Test
	void findFirstByEntityIdOrderByVersionDescNotFound() {

		final var revision = repository.findFirstByEntityIdOrderByVersionDesc("does-not-exist");

		assertThat(revision).isEmpty();
	}

	@Test
	void findAllByEntityId() {

		// Setup
		final var revisionEntityList = repository.findAllByEntityIdOrderByVersion(ENTITY_ID_2);

		assertThat(revisionEntityList)
			.isNotEmpty()
			.extracting(RevisionEntity::getEntityId, RevisionEntity::getVersion)
			.containsExactly(
				tuple(ENTITY_ID_2, 11),
				tuple(ENTITY_ID_2, 12));
	}

	@Test
	void findAllByEntityIdNotFound() {

		// Setup
		final var revisionEntityList = repository.findAllByEntityIdOrderByVersion("not-existing");

		assertThat(revisionEntityList).isEmpty();
	}

	private boolean isValidUUID(final String value) {
		try {
			UUID.fromString(String.valueOf(value));
		} catch (final Exception e) {
			return false;
		}

		return true;
	}
}
