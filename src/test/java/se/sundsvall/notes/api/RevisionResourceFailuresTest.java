package se.sundsvall.notes.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.notes.Application;
import se.sundsvall.notes.service.RevisionService;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class RevisionResourceFailuresTest {

	private static final String PATH = "/{municipalityId}/notes/{id}/revisions";
	private static final String MUNICIPALITY_ID = "2281";

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private RevisionService revisionServiceMock;

	@Test
	void getRevisionsByNoteIdInvalidId() {

		// Arrange
		final var id = "invalid";

		// Act
		final var response = webTestClient.get().uri(builder -> builder.path(PATH).build(Map.of("id", id, "municipalityId", MUNICIPALITY_ID)))
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
			.containsExactly(tuple("getRevisionsByNoteId.id", "not a valid UUID"));

		verifyNoInteractions(revisionServiceMock);
	}

	@Test
	void getDifferenceInvalidId() {

		// Arrange
		final var id = "invalid";

		// Act
		final var response = webTestClient.get().uri(builder -> builder.path(PATH + "/difference").queryParam("source", 1).queryParam("target", 2).build(Map.of("id", id, "municipalityId", MUNICIPALITY_ID)))
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
			.containsExactly(tuple("getDifferenceByVersions.id", "not a valid UUID"));

		verifyNoInteractions(revisionServiceMock);
	}

	@Test
	void getDifferenceNoParameters() {

		// Arrange
		final var id = UUID.randomUUID().toString();

		// Act
		final var response = webTestClient.get().uri(builder -> builder.path(PATH + "/difference").build(Map.of("id", id, "municipalityId", MUNICIPALITY_ID)))
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
		assertThat(response.getDetail()).isEqualTo("Required request parameter 'source' for method parameter type Integer is not present");

		verifyNoInteractions(revisionServiceMock);
	}

	@Test
	void getDifferenceNoTargetParameter() {

		// Arrange
		final var id = UUID.randomUUID().toString();

		// Act
		final var response = webTestClient.get().uri(builder -> builder.path(PATH + "/difference").queryParam("source", 1).build(Map.of("id", id, "municipalityId", MUNICIPALITY_ID)))
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
		assertThat(response.getDetail()).isEqualTo("Required request parameter 'target' for method parameter type Integer is not present");

		verifyNoInteractions(revisionServiceMock);
	}

	@Test
	void getDifferenceNoSourceParameter() {

		// Arrange
		final var id = UUID.randomUUID().toString();

		// Act
		final var response = webTestClient.get().uri(builder -> builder.path(PATH + "/difference").queryParam("target", 1).build(Map.of("id", id, "municipalityId", MUNICIPALITY_ID)))
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
		assertThat(response.getDetail()).isEqualTo("Required request parameter 'source' for method parameter type Integer is not present");

		verifyNoInteractions(revisionServiceMock);
	}

	@Test
	void getDifferenceNegativeValueInSource() {

		// Arrange
		final var id = UUID.randomUUID().toString();

		// Act
		final var response = webTestClient.get().uri(builder -> builder.path(PATH + "/difference").queryParam("source", -1).queryParam("target", 2).build(Map.of("id", id, "municipalityId", MUNICIPALITY_ID)))
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
			.containsExactly(tuple("getDifferenceByVersions.source", "must be between 0 and 2147483647"));

		verifyNoInteractions(revisionServiceMock);
	}

	@Test
	void getDifferenceNegativeValueInTarget() {

		// Arrange
		final var id = UUID.randomUUID().toString();

		// Act
		final var response = webTestClient.get().uri(builder -> builder.path(PATH + "/difference").queryParam("source", 1).queryParam("target", -2).build(Map.of("id", id, "municipalityId", MUNICIPALITY_ID)))
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
			.containsExactly(tuple("getDifferenceByVersions.target", "must be between 0 and 2147483647"));

		verifyNoInteractions(revisionServiceMock);
	}
}
