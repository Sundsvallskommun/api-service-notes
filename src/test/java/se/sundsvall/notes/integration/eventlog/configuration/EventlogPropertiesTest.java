package se.sundsvall.notes.integration.eventlog.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.notes.Application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class EventlogPropertiesTest {

	@Autowired
	private EventlogProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.connectTimeout()).isEqualTo(5);
		assertThat(properties.readTimeout()).isEqualTo(30);
	}
}
