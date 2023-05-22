package se.sundsvall.notes.service.mapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class OffsetDateTimeSerializer implements JsonSerializer<OffsetDateTime> {
	public static OffsetDateTimeSerializer create() {
		return new OffsetDateTimeSerializer();
	}

	@Override
	public JsonElement serialize(OffsetDateTime offsetDateTime, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(ISO_DATE_TIME.format(offsetDateTime));
	}
}