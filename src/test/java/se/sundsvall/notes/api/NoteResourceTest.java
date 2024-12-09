package se.sundsvall.notes.api;

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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import se.sundsvall.notes.Application;
import se.sundsvall.notes.api.model.CreateNoteRequest;
import se.sundsvall.notes.api.model.FindNotesRequest;
import se.sundsvall.notes.api.model.FindNotesResponse;
import se.sundsvall.notes.api.model.Note;
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.api.model.RevisionInformation;
import se.sundsvall.notes.api.model.UpdateNoteRequest;
import se.sundsvall.notes.service.NoteService;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class NoteResourceTest {

	private static final String PATH = "/2281/notes";
	private static final String MUNICIPALITY_ID = "2281";
	private static final String KEY_CURRENT_REVISION = "x-current-revision";
	private static final String KEY_CURRENT_VERSION = "x-current-version";
	private static final String KEY_PREVIOUS_REVISION = "x-previous-revision";
	private static final String KEY_PREVIOUS_VERSION = "x-previous-version";
	@MockBean
	private NoteService noteService;

	@Autowired
	private WebTestClient webTestClient;

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
			.withRole("role");

		when(noteService.createNote(any(CreateNoteRequest.class), anyString())).thenReturn(RevisionInformation.create()
			.withNote(Note.create().withId(id))
			.withCurrentRevision(Revision.create().withId("currentRevision").withVersion(0)));

		// Act
		webTestClient.post().uri(PATH)
			.contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL)
			.expectHeader().valueEquals(KEY_CURRENT_REVISION, "currentRevision")
			.expectHeader().valueEquals(KEY_CURRENT_VERSION, "0")
			.expectHeader().location("/2281/notes/".concat(id));

		// Assert
		verify(noteService).createNote(createNoteRequest, MUNICIPALITY_ID);
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
			.withRole("role");

		when(noteService.createNote(any(CreateNoteRequest.class), anyString())).thenReturn(RevisionInformation.create()
			.withNote(Note.create().withId(id))
			.withCurrentRevision(Revision.create().withId("currentRevision").withVersion(0)));

		// Act
		webTestClient.post().uri(PATH)
			.contentType(APPLICATION_JSON)
			.bodyValue(createNoteRequest)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(ALL)
			.expectHeader().valueEquals(KEY_CURRENT_REVISION, "currentRevision")
			.expectHeader().valueEquals(KEY_CURRENT_VERSION, "0")
			.expectHeader().location("/" + MUNICIPALITY_ID + "/notes/" + id);

		// Assert
		verify(noteService).createNote(createNoteRequest, MUNICIPALITY_ID);
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

		final var currentRevision = Revision.create().withId("currentRevision").withVersion(1);
		final var previousRevision = Revision.create().withId("previousRevision").withVersion(0);

		final Note note = Note.create().withId(id);

		when(noteService.updateNote(id, updateNoteRequest, MUNICIPALITY_ID)).thenReturn(RevisionInformation.create().withNote(note)
			.withCurrentRevision(currentRevision)
			.withPreviousRevision(previousRevision));

		// Act
		final var response = webTestClient.patch().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", id)))
			.contentType(APPLICATION_JSON)
			.bodyValue(updateNoteRequest)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectHeader().valueEquals(KEY_CURRENT_REVISION, "currentRevision")
			.expectHeader().valueEquals(KEY_CURRENT_VERSION, "1")
			.expectHeader().valueEquals(KEY_PREVIOUS_REVISION, "previousRevision")
			.expectHeader().valueEquals(KEY_PREVIOUS_VERSION, "0")
			.expectBody(Note.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull().isEqualTo(note);
		verify(noteService).updateNote(id, updateNoteRequest, MUNICIPALITY_ID);
	}

	@Test
	void deleteNoteByIdAndMunicipalityId() {

		// Arrange
		final var id = UUID.randomUUID().toString();

		when(noteService.deleteNoteByIdAndMunicipalityId(id, MUNICIPALITY_ID)).thenReturn(RevisionInformation.create()
			.withCurrentRevision(Revision.create().withId("currentRevision").withVersion(1)));

		// Act
		webTestClient.delete().uri(builder -> builder.path(PATH + "/{id}").build(Map.of("id", id)))
			.exchange()
			.expectStatus().isNoContent()
			.expectHeader().valueEquals(KEY_CURRENT_REVISION, "currentRevision")
			.expectHeader().valueEquals(KEY_CURRENT_VERSION, "1")
			.expectHeader().doesNotExist(CONTENT_TYPE);

		// Assert
		verify(noteService).deleteNoteByIdAndMunicipalityId(id, MUNICIPALITY_ID);
	}

	@Test
	void getNote() {

		// Arrange
		final var id = UUID.randomUUID().toString();

		final Note note = Note.create().withId(id);
		when(noteService.getNoteByIdAndMunicipalityId(id, MUNICIPALITY_ID)).thenReturn(note);

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
		verify(noteService).getNoteByIdAndMunicipalityId(id, MUNICIPALITY_ID);
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
		final var inParams = createParameterMap(page, limit, partyId, null, context, role, clientId);
		final var expectedResponse = FindNotesResponse.create().withNotes(List.of(Note.create()));

		when(noteService.getNotes(any(), anyString())).thenReturn(FindNotesResponse.create().withNotes(List.of(Note.create())));

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
		verify(noteService).getNotes(parametersCaptor.capture(), eq(MUNICIPALITY_ID));

		final FindNotesRequest findNotesRequest = parametersCaptor.getValue();
		assertThat(findNotesRequest.getPage()).isEqualTo(page);
		assertThat(findNotesRequest.getLimit()).isEqualTo(limit);
		assertThat(findNotesRequest.getPartyId()).isEqualTo(partyId);
		assertThat(findNotesRequest.getCaseId()).isNull();
		assertThat(findNotesRequest.getContext()).isEqualTo(context);
		assertThat(findNotesRequest.getRole()).isEqualTo(role);
		assertThat(findNotesRequest.getClientId()).isEqualTo(clientId);
	}

	private MultiValueMap<String, String> createParameterMap(final Integer page, final Integer limit, final String partyId, final String caseId, final String context, final String role, final String clientId) {
		final MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

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
