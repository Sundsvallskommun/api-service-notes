package se.sundsvall.notes.integration.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.notes.integration.db.model.NoteEntity;
import se.sundsvall.notes.integration.db.model.RevisionEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Revision repository tests.
 *
 * @see src/test/resources/db/scripts/RevisionRepositoryTest.sql for data setup.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/RevisionRepositoryTest.sql"
})
class RevisionRepositoryTest {

	private static final String ENTITY_ID_1 = "9791682e-4ba8-4f3a-857a-54e14836a53b";
	private static final String ENTITY_ID_2 = "abe72bbd-9808-4f3a-8aec-cd2945f5a201";
	private static final String MUNICIPALITY_ID = "2281";

	@Autowired
	private RevisionRepository repository;

	@Test
	void create() {

		final var result = repository.save(RevisionEntity.create()
			.withEntityId(randomUUID().toString())
			.withEntityType(NoteEntity.class.getSimpleName())
			.withSerializedSnapshot("{}")
			.withVersion(0)
			.withMunicipalityId(MUNICIPALITY_ID));

		assertThat(result).isNotNull();
		assertThat(result.getCreated()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
		assertThat(result.getEntityType()).isEqualTo("NoteEntity");
		assertThat(isValidUUID(result.getId())).isTrue();
		assertThat(isValidUUID(result.getEntityId())).isTrue();
	}

	@Test
	void findByEntityIdAndVersionAndMunicipalityId() {

		// Setup
		final var version = 3;

		final var revision = repository.findByEntityIdAndMunicipalityIdAndVersion(ENTITY_ID_1, MUNICIPALITY_ID, version);

		assertThat(revision)
			.isNotNull()
			.get()
			.extracting(RevisionEntity::getEntityId, RevisionEntity::getVersion)
			.containsExactly(ENTITY_ID_1, 3);
	}

	@Test
	void findByEntityIdAndVersionAndMunicipalityIdNotFound() {

		// Setup
		final var version = 666;

		final var revision = repository.findByEntityIdAndMunicipalityIdAndVersion(ENTITY_ID_1, MUNICIPALITY_ID, version);

		assertThat(revision).isEmpty();
	}

	@Test
	void findFirstByEntityIAndMunicipalityIddOrderByVersionDesc() {

		final var revision = repository.findFirstByEntityIdAndMunicipalityIdOrderByVersionDesc(ENTITY_ID_1, MUNICIPALITY_ID);

		assertThat(revision).isPresent();
		assertThat(revision.get().getEntityId()).isEqualTo(ENTITY_ID_1);
		assertThat(revision.get().getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(revision.get().getVersion()).isEqualTo(5);
	}

	@Test
	void findFirstByEntityIdAndMunicipalityIdOrderByVersionDescNotFound() {

		final var revision = repository.findFirstByEntityIdAndMunicipalityIdOrderByVersionDesc("does-not-exist", MUNICIPALITY_ID);

		assertThat(revision).isEmpty();
	}

	@Test
	void findAllByEntityIdAndMunicipalityId() {

		// Setup
		final var revisionEntityList = repository.findAllByEntityIdAndMunicipalityIdOrderByVersionDesc(ENTITY_ID_2, MUNICIPALITY_ID);

		assertThat(revisionEntityList)
			.isNotEmpty()
			.extracting(RevisionEntity::getEntityId, RevisionEntity::getMunicipalityId, RevisionEntity::getVersion)
			.containsExactly(
				tuple(ENTITY_ID_2, MUNICIPALITY_ID, 12),
				tuple(ENTITY_ID_2, MUNICIPALITY_ID, 11));
	}

	@Test
	void findAllByEntityIdAndMunicipalityIdNotFound() {

		// Setup
		final var revisionEntityList = repository.findAllByEntityIdAndMunicipalityIdOrderByVersionDesc("not-existing", MUNICIPALITY_ID);

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
