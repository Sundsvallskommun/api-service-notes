package se.sundsvall.notes.apptest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.http.HttpHeaders.LOCATION;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.notes.Application;
import se.sundsvall.notes.api.model.FindNotesRequest;
import se.sundsvall.notes.integration.db.NoteRepository;
import se.sundsvall.notes.integration.db.model.NoteEntity;

/**
 * Create note apptests.
 */
@WireMockAppTestSuite(files = "classpath:/CreateNoteIT/", classes = Application.class)
@ActiveProfiles("junit")
class CreateNoteIT extends AbstractAppTest {

	@Autowired
	private NoteRepository noteRepository;

	@Test
	void test1_createNote() throws Exception {

		final var partyId = "ffd20e9d-5987-417a-b8cd-a4617ac83a88";

		assertThat(noteRepository.findAllByParameters(FindNotesRequest.create().withPartyId(partyId), PageRequest.of(0, 100))).isEmpty();

		setupCall()
			.withServicePath("/notes")
			.withHttpMethod(HttpMethod.POST)
			.withRequest("request.json")
			.withExpectedResponseStatus(HttpStatus.CREATED)
			.withExpectedResponseHeader(LOCATION, List.of("^http://(.*)/notes/(.*)$"))
			.sendRequestAndVerifyResponse();

		assertThat(noteRepository.findAllByParameters(FindNotesRequest.create().withPartyId(partyId), PageRequest.of(0, 100))).hasSize(1)
			.extracting(
				NoteEntity::getBody,
				NoteEntity::getCaseId,
				NoteEntity::getCaseLink,
				NoteEntity::getCaseType,
				NoteEntity::getCreatedBy,
				NoteEntity::getExternalCaseId,
				NoteEntity::getPartyId,
				NoteEntity::getSubject)
			.containsExactly(tuple(
				"This is a note",
				"12345",
				"http://caselink.com/12345",
				"caseType",
				"John Doe",
				"54321",
				"ffd20e9d-5987-417a-b8cd-a4617ac83a88",
				"This is a subject"));
	}
}
