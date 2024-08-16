package se.sundsvall.notes.api;

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

import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class NoteResourceFailuresTest {

	private static final String PATH = "/2281/notes";
	private static final String MUNICIPALITY_ID = "2281";

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createNoteContextRoleAndClientIdMissing() {

		// Arrange
		final var createNoteRequest = CreateNoteRequest.create()
			.withBody("Test note")
			.withCreatedBy("createdBy")
			.withPartyId(UUID.randomUUID().toString())
			.withSubject("subject")
			.withCaseId("caseId")
			.withCaseType("caseType")
			.withCaseLink("caseLink")
			.withExternalCaseId("externalCaseId");

		// Act
		final var response = webTestClient.post().uri(builder -> builder.path(PATH).build())
			.contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
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

		// Arrange
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

		// Act
		final var response = webTestClient.post().uri(builder -> builder.path(PATH).build())
			.contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
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
	void createNoteInvalidPartyId() {

		// Arrange
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

		// Act
		final var response = webTestClient.post().uri(builder -> builder.path(PATH).build())
			.contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("partyId", "not a valid UUID"));
	}

	@Test
	void createNoteInvalidMunicipalityId() {

		// Arrange
		final var createNoteRequest = CreateNoteRequest.create()
			.withBody("Test note")
			.withContext("context")
			.withRole("role")
			.withClientId("clientId")
			.withCreatedBy("createdBy")
			.withSubject("subject")
			.withCaseId("caseId")
			.withCaseType("caseType")
			.withCaseLink("caseLink")
			.withExternalCaseId("externalCaseId");
		final var invalidPath = "/invalid-municipality-id/notes";

		// Act
		final var response = webTestClient.post().uri(builder -> builder.path(invalidPath).build())
			.contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("createNote.municipalityId", "not a valid municipality ID"));
	}

	@Test
	void createNoteToLongParameterValues() {

		// Arrange
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

		// Act
		final var response = webTestClient.post().uri(builder -> builder.path(PATH).build())
			.contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
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

		// Arrange
		final var createNoteRequest = "{}";

		// Act
		final var response = webTestClient.post().uri(builder -> builder.path(PATH).build())
			.contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
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
				tuple("subject", "must not be blank"));
	}

	@Test
	void createNoteNullBody() {

		// Act
		final var response = webTestClient.post().uri(builder -> builder.path(PATH).build())
			.contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).contains("Required request body is missing");
	}

	@Test
	void updateNoteInvalidId() {

		// Arrange
		final var id = "invalid";
		final var updateNoteRequest = UpdateNoteRequest.create()
			.withBody("body")
			.withSubject("subject")
			.withModifiedBy("modifiedBy");

		// Act
		final var response = webTestClient.patch().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", id)))
			.contentType(APPLICATION_JSON)
			.bodyValue(updateNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("updateNote.id", "not a valid UUID"));
	}

	@Test
	void updateNoteMissingProperties() {

		// Arrange
		final var id = UUID.randomUUID().toString();
		final var updateNoteRequest = UpdateNoteRequest.create();

		// Act
		final var response = webTestClient.patch().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", id))).contentType(APPLICATION_JSON)
			.bodyValue(updateNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
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

		// Arrange
		final var id = UUID.randomUUID().toString();
		final var updateNoteRequest = UpdateNoteRequest.create()
			.withBody(repeat("*", 2049))
			.withModifiedBy("modifiedBy")
			.withSubject(repeat("*", 256))
			.withCaseId(repeat("*", 256))
			.withCaseType(repeat("*", 256))
			.withCaseLink(repeat("*", 513))
			.withExternalCaseId(repeat("*", 256));

		// Act
		final var response = webTestClient.patch().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", id)))
			.contentType(APPLICATION_JSON)
			.bodyValue(updateNoteRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
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

		// Arrange
		final var id = UUID.randomUUID().toString();

		// Act
		final var response = webTestClient.patch().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", id)))
			.contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).contains("Required request body is missing");
	}

	@Test
	void getNoteByIdInvalidId() {

		// Arrange
		final var id = "invalid";

		// Act
		final var response = webTestClient.get().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", id)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("getNoteByIdAndMunicipalityId.id", "not a valid UUID"));
	}

	@Test
	void getNotesByPartyIdInvalidPartyId() {

		// Arrange
		final var partyId = "invalid";

		// Act
		final var response = webTestClient.get().uri(builder -> builder.path(PATH).queryParam("partyId", partyId).queryParam("municipalityId", MUNICIPALITY_ID).build())
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("partyId", "not a valid UUID"));
	}

	@Test
	void deleteNoteByIdInvalidId() {

		// Arrange
		final var id = "invalid";

		// Act
		final var response = webTestClient.delete().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", id)))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("deleteNoteByIdAndMunicipalityId.id", "not a valid UUID"));
	}

	@Test
	void deleteNoteByIdEmptyId() {

		// Arrange
		final var id = "";

		// Act
		final var response = webTestClient.delete().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", id)))
			.exchange()
			.expectStatus().isNotFound()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(response.getViolations()).isEmpty();
	}
}
