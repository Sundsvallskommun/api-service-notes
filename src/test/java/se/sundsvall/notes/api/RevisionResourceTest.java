package se.sundsvall.notes.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.notes.Application;
import se.sundsvall.notes.api.model.DifferenceResponse;
import se.sundsvall.notes.api.model.Revision;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class RevisionResourceTest {

	private static final String PATH = "/notes";

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getRevisions() {
		// Parameter values
		final var id = UUID.randomUUID().toString();

		final var response = webTestClient.get().uri(builder -> builder.path(PATH + "/{id}/revisions").build(Map.of("id", id)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBodyList(Revision.class)
			.returnResult()
			.getResponseBody();

		// Verification
		assertThat(response).isNotNull();
		//TODO: Add verification for call to service
	}

	@Test
	void getDifference() {
		// Parameter values
		final var id = UUID.randomUUID().toString();
		final var from = 1;
		final var to = 2;

		final var response = webTestClient.get().uri(builder -> builder.path(PATH + "/{id}/difference").queryParam("from", 1).queryParam("to", 2).build(Map.of("id", id)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(DifferenceResponse.class)
			.returnResult()
			.getResponseBody();

		// Verification
		assertThat(response).isNotNull();
		//TODO: Add verification for call to service
	}
}
