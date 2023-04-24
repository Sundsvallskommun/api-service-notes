package se.sundsvall.notes.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.notes.Application;
import se.sundsvall.notes.api.model.DifferenceResponse;
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.service.RevisionService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class RevisionResourceTest {

	private static final String PATH = "/notes/{id}/revisions";

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private RevisionService revisionServiceMock;

	@Test
	void getRevisions() {

		// Arrange
		final var id = UUID.randomUUID().toString();

		when(revisionServiceMock.getRevisions(any())).thenReturn(List.of(Revision.create()));

		// Act
		final var response = webTestClient.get().uri(builder -> builder.path(PATH).build(Map.of("id", id)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(Revision.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		verify(revisionServiceMock).getRevisions(id);
	}

	@Test
	void getDifference() {

		// Arrange
		final var id = UUID.randomUUID().toString();

		when(revisionServiceMock.diff(any(), anyInt(), anyInt())).thenReturn(DifferenceResponse.create());

		// Act
		final var response = webTestClient.get().uri(builder -> builder.path(PATH + "/difference").queryParam("source", 1).queryParam("target", 2).build(Map.of("id", id)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(DifferenceResponse.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		verify(revisionServiceMock).diff(id, 1, 2);
	}
}
