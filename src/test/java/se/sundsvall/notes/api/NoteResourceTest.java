package se.sundsvall.notes.api;

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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class NoteResourceTest {

	private static final String PATH = "/{municipalityId}/notes";
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
		final var id = UUID.randomUUID().toString();

		// Parameter values
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
			.withRole("role");

		// Mock
		when(noteService.createNote(anyString(), any())).thenReturn(id);

		webTestClient.post().uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL)
			.expectHeader().location("http://localhost:".concat(String.valueOf(port)).concat("/").concat(MUNICIPALITY_ID).concat("/notes/").concat(id));

		// Verification
		verify(noteService).createNote(MUNICIPALITY_ID, createNoteRequest);
	}

	@Test
	void createNoteWithoutPartyId() {
		final var id = UUID.randomUUID().toString();

		// Parameter values
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
			.withRole("role");

		// Mock
		when(noteService.createNote(anyString(), any())).thenReturn(id);

		webTestClient.post().uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID))).contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL)
			.expectHeader().location("http://localhost:".concat(String.valueOf(port)).concat("/").concat(MUNICIPALITY_ID).concat("/notes/").concat(id));

		// Verification
		verify(noteService).createNote(MUNICIPALITY_ID, createNoteRequest);
	}

	@Test
	void updateNote() {
		final var id = UUID.randomUUID().toString();

		// Parameter values
		final var updateNoteRequest = UpdateNoteRequest.create()
			.withBody("Test note")
			.withModifiedBy("modifiedBy")
			.withSubject("Test subject")
			.withCaseId("caseId")
			.withCaseType("caseType")
			.withCaseLink("caseLink")
			.withExternalCaseId("externalCaseId");

		// Mock
		Note note = Note.create().withId(id);
		when(noteService.updateNote(id, MUNICIPALITY_ID, updateNoteRequest)).thenReturn(note);

		final var response = webTestClient.patch().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("municipalityId", MUNICIPALITY_ID, "id", id)))
			.contentType(APPLICATION_JSON)
			.bodyValue(updateNoteRequest)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Note.class)
			.returnResult()
			.getResponseBody();

		// Verification
		assertThat(response).isNotNull().isEqualTo(note);
		verify(noteService).updateNote(id, MUNICIPALITY_ID, updateNoteRequest);
	}

	@Test
	void deleteNoteById() {

		// Parameter values
		final var id = UUID.randomUUID().toString();

		webTestClient.delete().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("municipalityId", MUNICIPALITY_ID,"id", id)))
			.exchange()
			.expectStatus().isNoContent()
			.expectHeader().doesNotExist(CONTENT_TYPE);

		// Verification
		verify(noteService).deleteNote(id, MUNICIPALITY_ID);
	}

	@Test
	void getNote() {

		// Parameter values
		final var id = UUID.randomUUID().toString();

		// Mock
		Note note = Note.create().withId(id);
		when(noteService.getNote(id, MUNICIPALITY_ID)).thenReturn(note);

		final var response = webTestClient.get().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("municipalityId", MUNICIPALITY_ID,"id", id)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(Note.class)
			.returnResult()
			.getResponseBody();

		// Verification
		assertThat(response).isNotNull().isEqualTo(note);
		verify(noteService).getNote(id, MUNICIPALITY_ID);
	}

	@Test
	void findNotes() {
		//Parameter values
		final var page = 1;
		final var limit = 10;
		final var partyId = UUID.randomUUID().toString();
		final var context = "context";
		final var role = "role";
		final var clientId = "clientId";

		final var inParams = createParameterMap(page, limit, partyId, null, context, role, clientId);

		final var expectedResponse = FindNotesResponse.create().withNotes(List.of(Note.create()));

		// Mock
		when(noteService.getNotes(anyString(), any())).thenReturn(FindNotesResponse.create().withNotes(List.of(Note.create())));

		final var response = webTestClient.get().uri(builder -> builder.path(PATH).queryParams(inParams).build(Map.of("municipalityId", MUNICIPALITY_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(FindNotesResponse.class)
			.returnResult()
			.getResponseBody();

		// Verification
		assertThat(response).isNotNull().isEqualTo(expectedResponse);
		verify(noteService).getNotes(eq(MUNICIPALITY_ID), parametersCaptor.capture());

		FindNotesRequest findNotesRequest = parametersCaptor.getValue();
		assertThat(findNotesRequest.getPage()).isEqualTo(page);
		assertThat(findNotesRequest.getLimit()).isEqualTo(limit);
		assertThat(findNotesRequest.getPartyId()).isEqualTo(partyId);
		assertThat(findNotesRequest.getCaseId()).isNull();
		assertThat(findNotesRequest.getContext()).isEqualTo(context);
		assertThat(findNotesRequest.getRole()).isEqualTo(role);
		assertThat(findNotesRequest.getClientId()).isEqualTo(clientId);
	}

	private MultiValueMap<String, String> createParameterMap(Integer page, Integer limit, String partyId, String caseId, String context, String role, String clientId) {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

		ofNullable(page).ifPresent(p -> parameters.add("page", p.toString()));
		ofNullable(limit).ifPresent(p -> parameters.add("limit", p.toString()));
		ofNullable(partyId).ifPresent(p -> parameters.add("partyId", p));
		ofNullable(caseId).ifPresent(p -> parameters.add("caseId", p));
		ofNullable(context).ifPresent(p -> parameters.add("context", p));
		ofNullable(role).ifPresent(p -> parameters.add("role", p));
		ofNullable(clientId).ifPresent(p -> parameters.add("clientId", p));

		return parameters;
	}
}
