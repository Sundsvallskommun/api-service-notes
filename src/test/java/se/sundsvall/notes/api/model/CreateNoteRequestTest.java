package se.sundsvall.notes.api.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class CreateNoteRequestTest {

	@Test
	void testBean() {
		assertThat(CreateNoteRequest.class, allOf(
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
		final var createdBy = "createdBy";
		final var externalCaseId = "externalCaseId";
		final var partyId = randomUUID().toString();
		final var role = "role";
		final var subject = "subject";
		final var municipalityId = "municipalityId";

		final var createNoteRequest = CreateNoteRequest.create()
			.withBody(body)
			.withCaseId(caseId)
			.withCaseType(caseType)
			.withCaseLink(caseLink)
			.withClientId(clientId)
			.withCreatedBy(createdBy)
			.withContext(context)
			.withExternalCaseId(externalCaseId)
			.withPartyId(partyId)
			.withRole(role)
			.withSubject(subject)
			.withMunicipalityId(municipalityId);

		assertThat(createNoteRequest).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(createNoteRequest.getBody()).isEqualTo(body);
		assertThat(createNoteRequest.getCaseId()).isEqualTo(caseId);
		assertThat(createNoteRequest.getCaseType()).isEqualTo(caseType);
		assertThat(createNoteRequest.getCaseLink()).isEqualTo(caseLink);
		assertThat(createNoteRequest.getClientId()).isEqualTo(clientId);
		assertThat(createNoteRequest.getCreatedBy()).isEqualTo(createdBy);
		assertThat(createNoteRequest.getContext()).isEqualTo(context);
		assertThat(createNoteRequest.getExternalCaseId()).isEqualTo(externalCaseId);
		assertThat(createNoteRequest.getPartyId()).isEqualTo(partyId);
		assertThat(createNoteRequest.getRole()).isEqualTo(role);
		assertThat(createNoteRequest.getSubject()).isEqualTo(subject);
		assertThat(createNoteRequest.getMunicipalityId()).isEqualTo(municipalityId);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(CreateNoteRequest.create()).hasAllNullFieldsOrProperties();
	}
}
