package se.sundsvall.notes.apptest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.notes.Application;

/**
 * Update note apptests.
 * 
 * @see src/test/resources/db/scripts/UpdateNoteAppTest.sql for data setup.
 */
@WireMockAppTestSuite(files = "classpath:/UpdateNoteIT/", classes = Application.class)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/UpdateNoteAppTest.sql"
})
class UpdateNoteIT extends AbstractAppTest {

	@Test
	void test01_updateById() throws Exception {

		setupCall()
			.withServicePath("/notes/8825bfae-11bc-4436-b1be-e4f0f225c048")
			.withHttpMethod(HttpMethod.PATCH)
			.withRequest("request.json")
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
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
