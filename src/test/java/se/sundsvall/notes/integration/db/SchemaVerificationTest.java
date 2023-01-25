package se.sundsvall.notes.integration.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("junit")
class SchemaVerificationTest {

	private static final String STORED_SCHEMA_FILE = "db/schema/schema.sql";

	@Value("${spring.jpa.properties.javax.persistence.schema-generation.scripts.create-target:}")
	private String generatedSchemaFile;

	@Test
	void verifySchemaUpdates() throws IOException {

		final var storedSchema = getResourceFile(STORED_SCHEMA_FILE);
		final var generatedSchema = getFile(generatedSchemaFile);

		assertThat(generatedSchema)
			.as(String.format("Please reflect modifications to entities in file: %s", STORED_SCHEMA_FILE))
			.hasSameTextualContentAs(storedSchema);
	}

	private File getResourceFile(String fileName) {
		return new File(getClass().getClassLoader().getResource(fileName).getFile());
	}

	private File getFile(String fileName) {
		return new File(fileName);
	}
}
