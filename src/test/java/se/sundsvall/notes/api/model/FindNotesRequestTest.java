package se.sundsvall.notes.api.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class FindNotesRequestTest {

	@Test
	void testBean() {
		assertThat(FindNotesRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var caseId = "caseId";
		final var clientId = "clientId";
		final var context = "context";
		final var limit = 13;
		final var page = 37;
		final var partyId = UUID.randomUUID().toString();
		final var role = "role";

		final var findNotesRequest = FindNotesRequest.create()
			.withCaseId(caseId)
			.withClientId(clientId)
			.withContext(context)
			.withLimit(limit)
			.withPage(page)
			.withPartyId(partyId)
			.withRole(role);

		assertThat(findNotesRequest).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(findNotesRequest.getCaseId()).isEqualTo(caseId);
		assertThat(findNotesRequest.getClientId()).isEqualTo(clientId);
		assertThat(findNotesRequest.getContext()).isEqualTo(context);
		assertThat(findNotesRequest.getLimit()).isEqualTo(limit);
		assertThat(findNotesRequest.getPage()).isEqualTo(page);
		assertThat(findNotesRequest.getPartyId()).isEqualTo(partyId);
		assertThat(findNotesRequest.getRole()).isEqualTo(role);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(FindNotesRequest.create()).hasAllNullFieldsOrPropertiesExcept("limit", "page");
		assertThat(FindNotesRequest.create().getLimit()).isEqualTo(100);
		assertThat(FindNotesRequest.create().getPage()).isEqualTo(1);
	}
}
