package se.sundsvall.notes.apptest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.notes.Application;
import se.sundsvall.notes.integration.db.NoteRepository;

/**
 * Delete note apptests.
 * 
 * @see src/test/resources/db/scripts/DeleteNoteAppTest.sql for data setup.
 */
@WireMockAppTestSuite(files = "classpath:/DeleteNoteIT/", classes = Application.class)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/DeleteNoteAppTest.sql"
})
class DeleteNoteIT extends AbstractAppTest {

	final static String MUNICIPALITY_ID = "2281";
	@Autowired
	private NoteRepository noteRepository;

	@Test
	void test01_deleteById() throws Exception {

		final var id = "2103ac13-1691-4017-b6c6-78fa75ff68fb";

		assertThat(noteRepository.findById(id)).isPresent();

		setupCall()
			.withServicePath("/".concat(MUNICIPALITY_ID).concat("/notes/").concat(id))
			.withHttpMethod(HttpMethod.DELETE)
			.withExpectedResponseStatus(HttpStatus.NO_CONTENT)
			.sendRequestAndVerifyResponse();

		assertThat(noteRepository.findById(id)).isNotPresent();
	}

	@Test
	void test02_deleteByIdNotFound() throws Exception {

		final var id = "33305f2f-59a4-44bc-b77d-64d99725f416"; // Id does not exist in DB.
		assertThat(noteRepository.findById(id)).isNotPresent();

		setupCall()
			.withServicePath("/".concat(MUNICIPALITY_ID).concat("/notes/").concat(id))
			.withHttpMethod(HttpMethod.DELETE)
			.withExpectedResponseStatus(HttpStatus.NOT_FOUND)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}
}
