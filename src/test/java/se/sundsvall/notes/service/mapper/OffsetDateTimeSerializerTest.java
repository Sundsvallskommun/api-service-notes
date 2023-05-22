package se.sundsvall.notes.service.mapper;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class OffsetDateTimeSerializerTest {

	private static final OffsetDateTimeSerializer INSTANCE = OffsetDateTimeSerializer.create();

	@Test
	void create() {
		assertThat(INSTANCE).isNotNull().isInstanceOf(OffsetDateTimeSerializer.class);
	}

	@Test
	void shouldSkipFieldForDeclaredMethodInDeclaredClass() {
		final var offsetDateTime = OffsetDateTime.now();
		final var serialized = INSTANCE.serialize(offsetDateTime, getClass(), null);

		assertThat(serialized.getAsString()).isEqualTo(offsetDateTime.format(DateTimeFormatter.ISO_DATE_TIME));
	}
}
