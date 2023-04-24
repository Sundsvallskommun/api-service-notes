package se.sundsvall.notes.apptest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.notes.Application;

/**
 * Note revisions apptests.
 *
 * @see src/test/resources/db/scripts/RevisionsAppTest.sql for data setup.
 */
@WireMockAppTestSuite(files = "classpath:/RevisionsIT/", classes = Application.class)
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/RevisionsAppTest.sql"
})
class RevisionsIT extends AbstractAppTest {

	private static final String REVISIONS_PATH = "/notes/9791682e-4ba8-4f3a-857a-54e14836a53b/revisions";

	@Test
	void test01_listRevisions() throws Exception {
		setupCall()
			.withServicePath(REVISIONS_PATH)
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_diffRevisions() throws Exception {
		setupCall()
			.withServicePath(REVISIONS_PATH + "/difference?source=1&target=2")
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_diffRevisionsNoDifference() throws Exception {
		setupCall()
			.withServicePath(REVISIONS_PATH + "/difference?source=3&target=4")
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}
}
