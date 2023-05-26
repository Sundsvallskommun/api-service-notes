package se.sundsvall.notes.apptest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.notes.Application;
import se.sundsvall.notes.api.model.FindNotesRequest;
import se.sundsvall.notes.integration.db.NoteRepository;
import se.sundsvall.notes.integration.db.RevisionRepository;
import se.sundsvall.notes.integration.db.model.NoteEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.http.HttpHeaders.LOCATION;

/**
 * Create note apptests.
 */
@WireMockAppTestSuite(files = "classpath:/CreateNoteIT/", classes = Application.class)
class CreateNoteIT extends AbstractAppTest {

	private final static String MUNICIPALITY_ID = "2281";

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private RevisionRepository revisionRepository;

	@Test
	void test01_createNoteWithPartyId() throws Exception {

		final var partyId = "ffd20e9d-5987-417a-b8cd-a4617ac83a88";

		assertThat(noteRepository.findAllByParameters(FindNotesRequest.create().withPartyId(partyId).withMunicipalityId(MUNICIPALITY_ID), PageRequest.of(0, 100))).isEmpty();

		setupCall()
			.withServicePath("/notes")
			.withHttpMethod(HttpMethod.POST)
			.withRequest("request.json")
			.withExpectedResponseStatus(HttpStatus.CREATED)
			.withExpectedResponseHeader(LOCATION, List.of("^http://(.*)/notes/(.*)$"))
			.sendRequestAndVerifyResponse();

		final var noteList = noteRepository.findAllByParameters(FindNotesRequest.create().withPartyId(partyId).withMunicipalityId(MUNICIPALITY_ID), PageRequest.of(0, 100));
		assertThat(noteList.getContent()).hasSize(1);
		final var note = noteList.getContent().get(0);

		assertThat(note.getBody()).isEqualTo("This is a note");
		assertThat(note.getCaseId()).isEqualTo("12345");
		assertThat(note.getCaseLink()).isEqualTo("http://caselink.com/12345");
		assertThat(note.getCreatedBy()).isEqualTo("John Doe");
		assertThat(note.getExternalCaseId()).isEqualTo("54321");
		assertThat(note.getPartyId()).isEqualTo("ffd20e9d-5987-417a-b8cd-a4617ac83a88");
		assertThat(note.getSubject()).isEqualTo("This is a subject");
		assertThat(note.getMunicipalityId()).isEqualTo("2281");

		// Assert that we only have the first version (version zero).
		assertThat(revisionRepository.findFirstByEntityIdOrderByVersionDesc(note.getId()).orElseThrow().getVersion()).isZero();
	}

	@Test
	void test02_createNoteWithoutPartyId() throws Exception {

		final var client = "MyGreatClient";

		assertThat(noteRepository.findAllByParameters(FindNotesRequest.create().withMunicipalityId(MUNICIPALITY_ID).withClientId(client), PageRequest.of(0, 100))).isEmpty();

		setupCall()
			.withServicePath("/notes")
			.withHttpMethod(HttpMethod.POST)
			.withRequest("request.json")
			.withExpectedResponseStatus(HttpStatus.CREATED)
			.withExpectedResponseHeader(LOCATION, List.of("^http://(.*)/notes/(.*)$"))
			.sendRequestAndVerifyResponse();

		assertThat(noteRepository.findAllByParameters(FindNotesRequest.create().withClientId(client), PageRequest.of(0, 100))).hasSize(1)
			.extracting(
				NoteEntity::getBody,
				NoteEntity::getCaseId,
				NoteEntity::getCaseLink,
				NoteEntity::getCaseType,
				NoteEntity::getCreatedBy,
				NoteEntity::getExternalCaseId,
				NoteEntity::getPartyId,
				NoteEntity::getSubject,
				NoteEntity::getMunicipalityId)
			.containsExactly(tuple(
				"This is another note",
				"54321",
				"http://caselink.com/54321",
				"caseType",
				"Jane Doe",
				"12345",
				null,
				"Lets make Notes great again",
				"2281"));
	}
}
