package se.sundsvall.notes.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.OffsetDateTime;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class NoteTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(Note.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var body = "body";
		final var caseId = "caseId";
		final var caseType = "caseType";
		final var caseLink = "caseLink";
		final var clientId = "clientId";
		final var context = "context";
		final var created = OffsetDateTime.now();
		final var createdBy = "createdBy";
		final var externalCaseId = "externalCaseId";
		final var id = UUID.randomUUID().toString();
		final var modified = OffsetDateTime.now();
		final var modifiedBy = "modifiedBy";
		final var partyId = UUID.randomUUID().toString();
		final var subject = "subject";
		final var role = "role";

		final var note = Note.create()
			.withBody(body)
			.withCaseId(caseId)
			.withCaseType(caseType)
			.withCaseLink(caseLink)
			.withClientId(clientId)
			.withContext(context)
			.withCreated(created)
			.withCreatedBy(createdBy)
			.withExternalCaseId(externalCaseId)
			.withId(id)
			.withModified(modified)
			.withModifiedBy(modifiedBy)
			.withPartyId(partyId)
			.withSubject(subject)
			.withRole(role);

		assertThat(note).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(note.getBody()).isEqualTo(body);
		assertThat(note.getCaseId()).isEqualTo(caseId);
		assertThat(note.getCaseType()).isEqualTo(caseType);
		assertThat(note.getCaseLink()).isEqualTo(caseLink);
		assertThat(note.getClientId()).isEqualTo(clientId);
		assertThat(note.getContext()).isEqualTo(context);
		assertThat(note.getCreated()).isEqualTo(created);
		assertThat(note.getCreatedBy()).isEqualTo(createdBy);
		assertThat(note.getExternalCaseId()).isEqualTo(externalCaseId);
		assertThat(note.getId()).isEqualTo(id);
		assertThat(note.getModified()).isEqualTo(modified);
		assertThat(note.getModifiedBy()).isEqualTo(modifiedBy);
		assertThat(note.getPartyId()).isEqualTo(partyId);
		assertThat(note.getSubject()).isEqualTo(subject);
		assertThat(note.getRole()).isEqualTo(role);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Note.create()).hasAllNullFieldsOrProperties();
	}
}
