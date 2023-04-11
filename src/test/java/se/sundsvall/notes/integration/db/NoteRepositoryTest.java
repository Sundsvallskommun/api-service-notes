package se.sundsvall.notes.integration.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.notes.Application;
import se.sundsvall.notes.api.model.FindNotesRequest;
import se.sundsvall.notes.integration.db.model.NoteEntity;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Note repository tests.
 * 
 * @see src/test/resources/db/scripts/NoteRepositoryTest.sql for data setup.
 */
@SpringBootTest(classes = {Application.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
		"/db/scripts/truncate.sql",
		"/db/scripts/NoteRepositoryTest.sql"
})
@Transactional
class NoteRepositoryTest {

	private static final String MUNICIPALITY_ID = "municipalityId1";
	private static final String ENTITY_1_ID = "a2f40fc7-7d70-404b-a294-85e4f7eff55e";
	private static final String ENTITY_1_PARTY_ID = "fbfbd90c-4c47-11ec-81d3-0242ac130003";
	private static final String ENTITY_2_ID = "2569abe8-eed4-46b6-9502-4cad428f9068";

	@Autowired
	private NoteRepository noteRepository;

	@Test
	void findById() {
		final var noteOptional = noteRepository.findById(ENTITY_1_ID);

		assertThat(noteOptional).isPresent();
		assertThat(noteOptional.get().getId()).isEqualTo(ENTITY_1_ID);
		assertThat(noteOptional.get().getPartyId()).isEqualTo(ENTITY_1_PARTY_ID);
	}

	@Test
	void findByIdNotFound() {
		final var noteEntityOptional = noteRepository.findById("does-not-exist");

		assertThat(noteEntityOptional).isNotPresent();
	}

	@Test
	void findByPartyId() {
		final var findNotesRequest = FindNotesRequest.create().withPartyId(ENTITY_1_PARTY_ID);
		final var page = noteRepository.findAllByParameters(MUNICIPALITY_ID, findNotesRequest, PageRequest.of(0, 100));

		final var noteEntities = page.getContent();

		assertThat(page.getNumber()).isZero();
		assertThat(page.getNumberOfElements()).isEqualTo(1);
		assertThat(page.getTotalPages()).isEqualTo(1);
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(noteEntities.get(0).getId()).isEqualTo(ENTITY_1_ID);
		assertThat(noteEntities.get(0).getPartyId()).isEqualTo(ENTITY_1_PARTY_ID);
		assertThat(noteEntities.get(0).getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
	}

	@Test
	void findByPartyIdNotFound() {
		final var findNotesRequest = FindNotesRequest.create().withPartyId("does-not-exist");
		final var page = noteRepository.findAllByParameters(MUNICIPALITY_ID, findNotesRequest, PageRequest.of(0, 100));

		assertThat(page.getContent()).isNotNull().isEmpty();
	}

	@Test
	void existsByIdAndMunicipalityId() {
		final var exists = noteRepository.existsByIdAndMunicipalityId(ENTITY_1_ID, MUNICIPALITY_ID);


		assertThat(exists).isTrue();
	}

	@Test
	void existsByIdAndMunicipalityIdNotFound() {
		final var exists = noteRepository.existsByIdAndMunicipalityId("does-not-exist", MUNICIPALITY_ID);

		assertThat(exists).isFalse();
	}

	@Test
	void persist() {
		final var noteEntity = NoteEntity.create()
				.withPartyId(UUID.randomUUID().toString())
				.withCreated(OffsetDateTime.now())
				.withCreatedBy("createdBy")
				.withSubject("subject")
				.withBody("body")
				.withCaseId("caseId")
				.withCaseType("caseType")
				.withCaseLink("caseLink")
				.withExternalCaseId("externalCaseId")
				.withMunicipalityId(MUNICIPALITY_ID);

		final var persistedEntity = noteRepository.saveAndFlush(noteEntity);

		assertThat(persistedEntity).isEqualTo(noteEntity);
		assertThat(persistedEntity.getCreated()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
		assertThat(isValidUUID(persistedEntity.getId())).isTrue();
		assertThat(persistedEntity.getModified()).isNull();
		assertThat(persistedEntity.getModifiedBy()).isNull();
	}

	@Test
	void update() {
		final var noteEntity = NoteEntity.create()
				.withPartyId(UUID.randomUUID().toString())
				.withCreatedBy("createdBy")
				.withSubject("subject")
				.withBody("body")
				.withCaseId("caseId")
				.withCaseType("caseType")
				.withCaseLink("caseLink")
				.withExternalCaseId("externalCaseId")
				.withMunicipalityId(MUNICIPALITY_ID);

		var persistedEntity = noteRepository.saveAndFlush(noteEntity);

		assertThat(persistedEntity).isEqualTo(noteEntity);
		assertThat(persistedEntity.getCreated()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
		assertThat(isValidUUID(persistedEntity.getId())).isTrue();
		assertThat(persistedEntity.getModified()).isNull();
		assertThat(persistedEntity.getModifiedBy()).isNull();

		persistedEntity.setSubject("modifiedSubject");
		persistedEntity.setModifiedBy("modifiedBy");

		final var updatedEntity = noteRepository.saveAndFlush(persistedEntity);

		assertThat(updatedEntity).isEqualTo(noteEntity);
		assertThat(updatedEntity.getCreated()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
		assertThat(isValidUUID(updatedEntity.getId())).isTrue();
		assertThat(updatedEntity.getModified()).isNotNull();
		assertThat(updatedEntity.getModified()).isCloseTo(OffsetDateTime.now(), within(2, SECONDS));
		assertThat(updatedEntity.getModifiedBy()).isEqualTo("modifiedBy");
		assertThat(updatedEntity.getSubject()).isEqualTo("modifiedSubject");
		assertThat(updatedEntity.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
	}

	@Test
	void delete() {
		assertThat(noteRepository.findById(ENTITY_2_ID)).isPresent();

		noteRepository.deleteById(ENTITY_2_ID);

		assertThat(noteRepository.findById(ENTITY_2_ID)).isNotPresent();
	}

	private boolean isValidUUID(String value) {
		try {
			UUID.fromString(String.valueOf(value));
		} catch (Exception e) {
			return false;
		}

		return true;
	}
}
