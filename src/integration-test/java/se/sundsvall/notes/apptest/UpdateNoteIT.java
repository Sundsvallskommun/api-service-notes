package se.sundsvall.notes.apptest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.notes.Application;
import se.sundsvall.notes.integration.db.RevisionRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

	private static final String REQUEST= "request.json";
	private static final String RESPONSE = "response.json";
	private static final String MUNICIPALITY_ID = "2281";

	@Autowired
	private RevisionRepository revisionRepository;

	@Test
	void test01_updateById() {

		final var entityId = "8825bfae-11bc-4436-b1be-e4f0f225c048";

		// Assert that we only have the first version (version zero).
		assertThat(revisionRepository.findFirstByEntityIdAndMunicipalityIdOrderByVersionDesc(entityId, MUNICIPALITY_ID).orElseThrow().getVersion()).isZero();

		setupCall()
			.withServicePath("/2281/notes/" + entityId)
			.withHttpMethod(HttpMethod.PATCH)
			.withRequest(REQUEST)
			.withExpectedResponseStatus(HttpStatus.OK)
			.withExpectedResponseHeader("x-current-revision", List.of("(.*)-(.*)-(.*)-(.*)-(.*)"))
			.withExpectedResponseHeader("x-current-version", List.of("1"))
			.withExpectedResponseHeader("x-previous-revision", List.of("6e18bfaf-2480-424a-83c1-fb234c75befc"))
			.withExpectedResponseHeader("x-previous-version", List.of("0"))
			.withExpectedResponse(RESPONSE)
			.sendRequestAndVerifyResponse();

		// Assert that a new version was created.
		assertThat(revisionRepository.findFirstByEntityIdAndMunicipalityIdOrderByVersionDesc(entityId, MUNICIPALITY_ID).orElseThrow().getVersion()).isEqualTo(1);
	}

	@Test
	void test02_updateByIdNotFound() {
		setupCall()
			.withServicePath("/2281/notes/9eceeeb1-f939-441c-858f-da3deb05e578") // Id does not exist in DB.
			.withHttpMethod(HttpMethod.PATCH)
			.withRequest(REQUEST)
			.withExpectedResponseStatus(HttpStatus.NOT_FOUND)
			.withExpectedResponse(RESPONSE)
			.sendRequestAndVerifyResponse();
	}
}
