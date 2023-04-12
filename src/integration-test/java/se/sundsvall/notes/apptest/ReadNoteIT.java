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
 * Read note apptests.
 * 
 * @see src/test/resources/db/scripts/ReadNoteAppTest.sql for data setup.
 */
@WireMockAppTestSuite(files = "classpath:/ReadNoteIT/", classes = Application.class)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/ReadNoteAppTest.sql"
})
class ReadNoteIT extends AbstractAppTest {

	private final static String MUNICIPALITY_ID = "2281";

	@Test
	void test01_readById() throws Exception {
		setupCall()
			.withServicePath("/notes/134aedef-2d33-410b-8654-207e9644fc3d")
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_readAllFilterByPartyIdContextRoleClientId() throws Exception {
		setupCall()
			.withServicePath("/notes?partyId=a37b06cc-edda-459d-860c-9f8cd1e24b00&context=context1&role=role1&clientId=clientId1&municipalityId=" + MUNICIPALITY_ID)
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_readByIdNotFound() throws Exception {
		setupCall()
			.withServicePath("/notes/14c8fd87-2a57-4e38-bcd5-6fff9ca6880e") // Id does not exist in DB.
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.NOT_FOUND)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_readAllNoneFound() throws Exception {
		setupCall()
			.withServicePath("/notes?partyId=e1cf339a-0ac7-4f3d-a6f6-9f687f9097e0&municipalityId=" + MUNICIPALITY_ID) // partyId does not exist in DB.
			.withHttpMethod(HttpMethod.GET)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}
}
