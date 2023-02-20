package se.sundsvall.notes.api;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import se.sundsvall.notes.Application;
import se.sundsvall.notes.api.model.CreateNoteRequest;
import se.sundsvall.notes.api.model.UpdateNoteRequest;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class NoteResourceFailuresTest {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createNoteContextRoleAndClientIdMissing() {

		// Parameter values
		final var createNoteRequest = CreateNoteRequest.create()
			.withBody("Test note")
			.withCreatedBy("createdBy")
			.withPartyId(UUID.randomUUID().toString())
			.withSubject("subject")
			.withCaseId("caseId")
			.withCaseType("caseType")
			.withCaseLink("caseLink")
			.withExternalCaseId("externalCaseId");

		final var response = webTestClient.post().uri("/notes").contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(
				tuple("context", "must not be blank"),
				tuple("role", "must not be blank"),
				tuple("clientId", "must not be blank"));
	}

	@Test
	void createNoteContextRoleAndClientIdEmptyString() {

		// Parameter values
		final var createNoteRequest = CreateNoteRequest.create()
			.withBody("Test note")
			.withContext("     ")
			.withRole("     ")
			.withClientId("     ")
			.withCreatedBy("createdBy")
			.withPartyId(UUID.randomUUID().toString())
			.withSubject("subject")
			.withCaseId("caseId")
			.withCaseType("caseType")
			.withCaseLink("caseLink")
			.withExternalCaseId("externalCaseId");

		final var response = webTestClient.post().uri("/notes").contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(
				tuple("context", "must not be blank"),
				tuple("role", "must not be blank"),
				tuple("clientId", "must not be blank"));
	}

	@Test
	void createNoteMissingPartyId() {

		// Parameter values
		final var createNoteRequest = CreateNoteRequest.create()
			.withBody("Test note")
			.withContext("context")
			.withRole("role")
			.withClientId("clientId")
			.withCreatedBy("createdBy")
			.withPartyId(null) // Missing partyId
			.withSubject("subject")
			.withCaseId("caseId")
			.withCaseType("caseType")
			.withCaseLink("caseLink")
			.withExternalCaseId("externalCaseId");

		final var response = webTestClient.post().uri("/notes").contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("partyId", "not a valid UUID"));
	}

	@Test
	void createNoteInvalidPartyId() {

		// Parameter values
		final var createNoteRequest = CreateNoteRequest.create()
			.withBody("Test note")
			.withContext("context")
			.withRole("role")
			.withClientId("clientId")
			.withCreatedBy("createdBy")
			.withPartyId("invalid")
			.withSubject("subject")
			.withCaseId("caseId")
			.withCaseType("caseType")
			.withCaseLink("caseLink")
			.withExternalCaseId("externalCaseId");

		final var response = webTestClient.post().uri("/notes").contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("partyId", "not a valid UUID"));
	}

	@Test
	void createNoteToLongParameterValues() {

		// Parameter values
		final var createNoteRequest = CreateNoteRequest.create()
			.withBody(repeat("*", 2049))
			.withCreatedBy("createdBy")
			.withPartyId(UUID.randomUUID().toString())
			.withSubject(repeat("*", 256))
			.withContext(repeat("*", 256))
			.withRole(repeat("*", 256))
			.withClientId(repeat("*", 256))
			.withCaseId(repeat("*", 256))
			.withCaseType(repeat("*", 256))
			.withCaseLink(repeat("*", 513))
			.withExternalCaseId(repeat("*", 256));

		final var response = webTestClient.post().uri("/notes").contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(
				tuple("body", "size must be between 1 and 2048"),
				tuple("caseId", "size must be between 1 and 255"),
				tuple("context", "size must be between 1 and 255"),
				tuple("role", "size must be between 1 and 255"),
				tuple("caseLink", "size must be between 1 and 512"),
				tuple("caseType", "size must be between 1 and 255"),
				tuple("clientId", "size must be between 1 and 255"),
				tuple("externalCaseId", "size must be between 1 and 255"),
				tuple("subject", "size must be between 1 and 255"));
	}

	@Test
	void createNoteEmptyJsonBody() {

		// Parameter values
		final var createNoteRequest = "{}";

		final var response = webTestClient.post().uri("/notes").contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(
				tuple("body", "must not be blank"),
				tuple("context", "must not be blank"),
				tuple("role", "must not be blank"),
				tuple("clientId", "must not be blank"),
				tuple("createdBy", "must not be blank"),
				tuple("partyId", "not a valid UUID"),
				tuple("subject", "must not be blank"));
	}

	@Test
	void createNoteNullBody() {

		final var response = webTestClient.post().uri("/notes").contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).contains("Required request body is missing");
	}

	@Test
	void updateNoteInvalidId() {

		// Parameter values
		final var id = "invalid";
		final var updateNoteRequest = UpdateNoteRequest.create()
			.withBody("body")
			.withSubject("subject")
			.withModifiedBy("modifiedBy");

		final var response = webTestClient.patch().uri(builder -> builder.path("/notes/{id}").build(Map.of("id", id))).contentType(APPLICATION_JSON)
			.bodyValue(updateNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("updateNote.id", "not a valid UUID"));
	}

	@Test
	void updateNoteMissingProperties() {

		// Parameter values
		final var id = UUID.randomUUID().toString();
		final var updateNoteRequest = UpdateNoteRequest.create();

		final var response = webTestClient.patch().uri(builder -> builder.path("/notes/{id}").build(Map.of("id", id))).contentType(APPLICATION_JSON)
			.bodyValue(updateNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(
				tuple("modifiedBy", "must not be blank"));
	}

	@Test
	void updateNoteTooLongParameterValues() {

		// Parameter values
		final var id = UUID.randomUUID().toString();
		final var updateNoteRequest = UpdateNoteRequest.create()
			.withBody(repeat("*", 2049))
			.withModifiedBy("modifiedBy")
			.withSubject(repeat("*", 256))
			.withCaseId(repeat("*", 256))
			.withCaseType(repeat("*", 256))
			.withCaseLink(repeat("*", 513))
			.withExternalCaseId(repeat("*", 256));

		final var response = webTestClient.patch().uri(builder -> builder.path("/notes/{id}").build(Map.of("id", id))).contentType(APPLICATION_JSON)
			.bodyValue(updateNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactlyInAnyOrder(
				tuple("body", "size must be between 1 and 2048"),
				tuple("caseId", "size must be between 1 and 255"),
				tuple("caseLink", "size must be between 1 and 512"),
				tuple("caseType", "size must be between 1 and 255"),
				tuple("externalCaseId", "size must be between 1 and 255"),
				tuple("subject", "size must be between 1 and 255"));
	}

	@Test
	void updateNoteNullBody() {

		// Parameter values
		final var id = UUID.randomUUID().toString();

		final var response = webTestClient.patch().uri(builder -> builder.path("/notes/{id}").build(Map.of("id", id))).contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).contains("Required request body is missing");
	}

	@Test
	void getNoteByIdInvalidId() {

		// Parameter values
		final var id = "invalid";

		final var response = webTestClient.get().uri(builder -> builder.path("/notes/{id}").build(Map.of("id", id)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("getNoteById.id", "not a valid UUID"));
	}

	@Test
	void getNotesByPartyIdInvalidPartyId() {

		// Parameter values
		final var partyId = "invalid";

		final var response = webTestClient.get().uri(builder -> builder.path("/notes").queryParam("partyId", partyId) .build())
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("partyId", "not a valid UUID"));
	}

	@Test
	void deleteNoteByIdInvalidId() {

		// Parameter values
		final var id = "invalid";

		final var response = webTestClient.delete().uri(builder -> builder.path("/notes/{id}").build(Map.of("id", id)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("deleteNoteById.id", "not a valid UUID"));
	}

	@Test
	void deleteNoteByIdEmptyId() {

		// Parameter values
		final var id = "";

		final var response = webTestClient.delete().uri(builder -> builder.path("/notes/{id}").build(Map.of("id", id)))
				.exchange()
				.expectStatus().isEqualTo(METHOD_NOT_ALLOWED)
				.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
				.expectBody(ConstraintViolationProblem.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus().getStatusCode()).isEqualTo(METHOD_NOT_ALLOWED.value());
		assertThat(response.getViolations()).isEmpty();
	}
}
