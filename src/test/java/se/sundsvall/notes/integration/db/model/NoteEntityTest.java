package se.sundsvall.notes.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import java.time.OffsetDateTime;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class NoteEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(NoteEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var id = UUID.randomUUID().toString();
		final var partyId = UUID.randomUUID().toString();
		final var context = "context";
		final var role = "role";
		final var clientId = "clientId";
		final var created = OffsetDateTime.now();
		final var createdBy = "createdBy";
		final var modified = OffsetDateTime.now().plusDays(1);
		final var modifiedBy = "modifiedBy";
		final var subject = "subject";
		final var body = "body";
		final var caseId = "caseId";
		final var caseType = "caseType";
		final var caseLink = "caseLink";
		final var externalCaseId = "externalCaseId";
		final var municipalityId = "municipalityId";

		final var noteEntity = NoteEntity.create()
			.withId(id)
			.withPartyId(partyId)
			.withContext(context)
			.withClientId(clientId)
			.withRole(role)
			.withCreated(created)
			.withCreatedBy(createdBy)
			.withModified(modified)
			.withModifiedBy(modifiedBy)
			.withSubject(subject)
			.withBody(body)
			.withCaseId(caseId)
			.withCaseType(caseType)
			.withCaseLink(caseLink)
			.withExternalCaseId(externalCaseId)
			.withMunicipalityId(municipalityId);

		assertThat(noteEntity).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(noteEntity.getId()).isEqualTo(id);
		assertThat(noteEntity.getPartyId()).isEqualTo(partyId);
		assertThat(noteEntity.getContext()).isEqualTo(context);
		assertThat(noteEntity.getClientId()).isEqualTo(clientId);
		assertThat(noteEntity.getRole()).isEqualTo(role);
		assertThat(noteEntity.getCreated()).isEqualTo(created);
		assertThat(noteEntity.getCreatedBy()).isEqualTo(createdBy);
		assertThat(noteEntity.getModified()).isEqualTo(modified);
		assertThat(noteEntity.getModifiedBy()).isEqualTo(modifiedBy);
		assertThat(noteEntity.getSubject()).isEqualTo(subject);
		assertThat(noteEntity.getBody()).isEqualTo(body);
		assertThat(noteEntity.getCaseId()).isEqualTo(caseId);
		assertThat(noteEntity.getCaseType()).isEqualTo(caseType);
		assertThat(noteEntity.getCaseLink()).isEqualTo(caseLink);
		assertThat(noteEntity.getExternalCaseId()).isEqualTo(externalCaseId);
		assertThat(noteEntity.getMunicipalityId()).isEqualTo(municipalityId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(NoteEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new NoteEntity()).hasAllNullFieldsOrProperties();
	}
}
