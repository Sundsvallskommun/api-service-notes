package se.sundsvall.notes.apptest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.notes.Application;
import se.sundsvall.notes.integration.db.RevisionRepository;

/**
 * Update note apptests.
 *
 * @see src/test/resources/db/scripts/UpdateNoteAppTest.sql for data setup.
 */
@WireMockAppTestSuite(files = "classpath:/UpdateNoteIT/", classes = Application.class)
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/UpdateNoteAppTest.sql"
})
class UpdateNoteIT extends AbstractAppTest {

	@Autowired
	private RevisionRepository revisionRepository;

	@Test
	void test01_updateById() throws Exception {

		final var entityId = "8825bfae-11bc-4436-b1be-e4f0f225c048";

		// Assert that we only have the first version (version zero).
		assertThat(revisionRepository.findFirstByEntityIdOrderByVersionDesc(entityId).orElseThrow().getVersion()).isZero();

		setupCall()
			.withServicePath("/notes/" + entityId)
			.withHttpMethod(HttpMethod.PATCH)
			.withRequest("request.json")
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();

		// Assert that a new version was created.
		assertThat(revisionRepository.findFirstByEntityIdOrderByVersionDesc(entityId).orElseThrow().getVersion()).isEqualTo(1);
	}

	@Test
	void test02_updateByIdNotFound() throws Exception {
		setupCall()
			.withServicePath("/notes/9eceeeb1-f939-441c-858f-da3deb05e578") // Id does not exist in DB.
			.withHttpMethod(HttpMethod.PATCH)
			.withRequest("request.json")
			.withExpectedResponseStatus(HttpStatus.NOT_FOUND)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}
}
