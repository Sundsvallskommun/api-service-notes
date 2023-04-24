package se.sundsvall.notes.api;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import se.sundsvall.notes.Application;
import se.sundsvall.notes.api.model.CreateNoteRequest;
import se.sundsvall.notes.api.model.FindNotesRequest;
import se.sundsvall.notes.api.model.FindNotesResponse;
import se.sundsvall.notes.api.model.Note;
import se.sundsvall.notes.api.model.UpdateNoteRequest;
import se.sundsvall.notes.service.NoteService;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class NoteResourceTest {

	private static final String PATH = "/notes";
	private static final String MUNICIPALITY_ID = "2281";
	@MockBean
	private NoteService noteService;

	@Autowired
	private WebTestClient webTestClient;

	@LocalServerPort
	private int port;

	@Captor
	private ArgumentCaptor<FindNotesRequest> parametersCaptor;

	@Test
	void createNoteWithPartyId() {

		// Arrange
		final var id = UUID.randomUUID().toString();

		final var createNoteRequest = CreateNoteRequest.create()
			.withBody("Test note")
			.withCaseId("caseId")
			.withCaseType("caseType")
			.withCaseLink("caseLink")
			.withClientId("clientId")
			.withContext("context")
			.withCreatedBy("createdBy")
			.withExternalCaseId("externalCaseId")
			.withPartyId(UUID.randomUUID().toString())
			.withSubject("Test subject")
			.withRole("role")
			.withMunicipalityId(MUNICIPALITY_ID);

		when(noteService.createNote(any(CreateNoteRequest.class))).thenReturn(id);

		// Act
		webTestClient.post().uri(PATH)
			.contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL)
			.expectHeader().location("http://localhost:".concat(String.valueOf(port)).concat("/notes/").concat(id));

		// Assert
		verify(noteService).createNote(createNoteRequest);
	}

	@Test
	void createNoteWithoutPartyId() {

		// Arrange
		final var id = UUID.randomUUID().toString();

		final var createNoteRequest = CreateNoteRequest.create()
			.withBody("Test note")
			.withCaseId("caseId")
			.withCaseType("caseType")
			.withCaseLink("caseLink")
			.withClientId("clientId")
			.withContext("context")
			.withCreatedBy("createdBy")
			.withExternalCaseId("externalCaseId")
			.withSubject("Test subject")
			.withRole("role")
			.withMunicipalityId(MUNICIPALITY_ID);

		when(noteService.createNote(any(CreateNoteRequest.class))).thenReturn(id);

		// Act
		webTestClient.post().uri(PATH)
			.contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL)
			.expectHeader().location("http://localhost:".concat(String.valueOf(port)).concat("/notes/").concat(id));

		// Assert
		verify(noteService).createNote(createNoteRequest);
	}

	@Test
	void updateNote() {

		// Arrange
		final var id = UUID.randomUUID().toString();

		final var updateNoteRequest = UpdateNoteRequest.create()
			.withBody("Test note")
			.withModifiedBy("modifiedBy")
			.withSubject("Test subject")
			.withCaseId("caseId")
			.withCaseType("caseType")
			.withCaseLink("caseLink")
			.withExternalCaseId("externalCaseId");

		final Note note = Note.create().withId(id);
		when(noteService.updateNote(id, updateNoteRequest)).thenReturn(note);

		// Act
		final var response = webTestClient.patch().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", id)))
			.contentType(APPLICATION_JSON)
			.bodyValue(updateNoteRequest)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Note.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull().isEqualTo(note);
		verify(noteService).updateNote(id, updateNoteRequest);
	}

	@Test
	void deleteNoteById() {

		// Arrange
		final var id = UUID.randomUUID().toString();

		// Act
		webTestClient.delete().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", id)))
			.exchange()
			.expectStatus().isNoContent()
			.expectHeader().doesNotExist(CONTENT_TYPE);

		// Assert
		verify(noteService).deleteNoteById(id);
	}

	@Test
	void getNote() {

		// Arrange
		final var id = UUID.randomUUID().toString();

		final Note note = Note.create().withId(id);
		when(noteService.getNoteById(id)).thenReturn(note);

		// Act
		final var response = webTestClient.get().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", id)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Note.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull().isEqualTo(note);
		verify(noteService).getNoteById(id);
	}

	@Test
	void findNotes() {

		// Arrange
		final var page = 1;
		final var limit = 10;
		final var partyId = UUID.randomUUID().toString();
		final var context = "context";
		final var role = "role";
		final var clientId = "clientId";
		final var inParams = createParameterMap(page, limit, partyId, null, context, role, clientId, MUNICIPALITY_ID);
		final var expectedResponse = FindNotesResponse.create().withNotes(List.of(Note.create()));

		when(noteService.getNotes(any())).thenReturn(FindNotesResponse.create().withNotes(List.of(Note.create())));

		// Act
		final var response = webTestClient.get().uri(builder -> builder.path(PATH).queryParams(inParams).build())
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(FindNotesResponse.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull().isEqualTo(expectedResponse);
		verify(noteService).getNotes(parametersCaptor.capture());

		final FindNotesRequest findNotesRequest = parametersCaptor.getValue();
		assertThat(findNotesRequest.getPage()).isEqualTo(page);
		assertThat(findNotesRequest.getLimit()).isEqualTo(limit);
		assertThat(findNotesRequest.getPartyId()).isEqualTo(partyId);
		assertThat(findNotesRequest.getCaseId()).isNull();
		assertThat(findNotesRequest.getContext()).isEqualTo(context);
		assertThat(findNotesRequest.getRole()).isEqualTo(role);
		assertThat(findNotesRequest.getClientId()).isEqualTo(clientId);
		assertThat(findNotesRequest.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
	}

	private MultiValueMap<String, String> createParameterMap(final Integer page, final Integer limit, final String partyId, final String caseId, final String context, final String role, final String clientId, final String municipalityId) {
		final MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

		ofNullable(page).ifPresent(p -> parameters.add("page", p.toString()));
		ofNullable(limit).ifPresent(p -> parameters.add("limit", p.toString()));
		ofNullable(partyId).ifPresent(p -> parameters.add("partyId", p));
		ofNullable(caseId).ifPresent(p -> parameters.add("caseId", p));
		ofNullable(context).ifPresent(p -> parameters.add("context", p));
		ofNullable(role).ifPresent(p -> parameters.add("role", p));
		ofNullable(clientId).ifPresent(p -> parameters.add("clientId", p));
		ofNullable(municipalityId).ifPresent(p -> parameters.add("municipalityId", p));

		return parameters;
	}
}
