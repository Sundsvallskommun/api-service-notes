package se.sundsvall.notes.api;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.problem.violations.Violation;
import se.sundsvall.notes.Application;
import se.sundsvall.notes.service.RevisionService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@AutoConfigureWebTestClient
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class RevisionResourceFailuresTest {

	private static final String PATH = "/{municipalityId}/notes/{id}/revisions";
	private static final String MUNICIPALITY_ID = "2281";

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
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
			.extracting(Violation::field, Violation::message)
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
			.extracting(Violation::field, Violation::message)
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
		assertThat(response.getDetail()).isEqualTo("Required parameter 'source' is not present.");

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
		assertThat(response.getDetail()).isEqualTo("Required parameter 'target' is not present.");

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
		assertThat(response.getDetail()).isEqualTo("Required parameter 'source' is not present.");

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
			.extracting(Violation::field, Violation::message)
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
			.extracting(Violation::field, Violation::message)
			.containsExactly(tuple("getDifferenceByVersions.target", "must be between 0 and 2147483647"));

		verifyNoInteractions(revisionServiceMock);
	}
}
