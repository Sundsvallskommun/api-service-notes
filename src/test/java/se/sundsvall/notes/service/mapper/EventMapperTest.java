package se.sundsvall.notes.service.mapper;

import generated.se.sundsvall.eventlog.EventType;
import generated.se.sundsvall.eventlog.Metadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.sundsvall.notes.api.model.Note;
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.integration.db.model.NoteEntity;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.groups.Tuple.tuple;

class EventMapperTest {

	@Test
	void toEvent() {

		// Setup
		final var eventType = EventType.CREATE;
		final var message = "message";
		final var revision = "revision";
		final var metaData = Map.of("key", "value");
		final var executedByUserId = "executedByUserId";
		final var owner = "Notes";
		final var sourceType = Note.class.getSimpleName();

		// Call
		final var event = EventMapper.toEvent(eventType, message, revision, metaData, executedByUserId);

		// Verification
		assertThat(event).isNotNull();
		assertThat(event.getCreated()).isNotNull().isCloseTo(OffsetDateTime.now(), within(1, SECONDS))   ;
		assertThat(event.getType()).isEqualTo(eventType);
		assertThat(event.getMessage()).isEqualTo(message);
		assertThat(event.getHistoryReference()).isEqualTo(revision);
		assertThat(event.getOwner()).isEqualTo(owner);
		assertThat(event.getSourceType()).isEqualTo(sourceType);
		assertThat(event.getMetadata()).hasSize(2).extracting(
				Metadata::getKey,
				Metadata::getValue)
			.containsExactly(tuple("key", "value"),
				tuple("ExecutedBy", executedByUserId));
	}

	@Test
	void toEventWithNullValues() {

		// Call
		final var event = EventMapper.toEvent(null, null, null, null, null);

		// Verification
		assertThat(event).isNull();
	}

	@Test
	void toEventWithEmptyValues() {

		// Call
		final var event = EventMapper.toEvent(EventType.CREATE, "", "", Map.of(), "");

		// Verification
		assertThat(event).isNotNull();
		assertThat(event.getCreated()).isNotNull().isCloseTo(OffsetDateTime.now(), within(1, SECONDS))   ;
		assertThat(event.getType()).isEqualTo(EventType.CREATE);
		assertThat(event.getMessage()).isEmpty();
		assertThat(event.getHistoryReference()).isEmpty();
		assertThat(event.getOwner()).isEqualTo("Notes");
		assertThat(event.getSourceType()).isEqualTo(Note.class.getSimpleName());
		assertThat(event.getMetadata()).hasSize(1).extracting(
				Metadata::getKey,
				Metadata::getValue)
			.containsExactly(tuple("ExecutedBy", ""));
	}

	@ParameterizedTest
	@MethodSource("metadataMapArgumentProvider")
	void toMetadataMap(NoteEntity noteEntity, Revision currentRevision, Revision previousRevision, Map<String, String> result) {
		assertThat(EventMapper.toMetadataMap(noteEntity, currentRevision, previousRevision)).containsExactlyInAnyOrderEntriesOf(result);
	}

	private static Stream<Arguments> metadataMapArgumentProvider() {
		final var CASE_ID = "caseId";
		final var PREVIOUS_ID = "previousId";
		final var PREVIOUS_VERSION = 1;
		final var KEY_CURRENT_REVISION = "CurrentRevision";
		final var KEY_CURRENT_VERSION = "CurrentVersion";
		final var KEY_PREVIOUS_VERSION = "PreviousVersion";
		final var KEY_PREVIOUS_REVISION = "PreviousRevision";
		final var CURRENT_ID = "currentId";
		final var CURRENT_VERSION = 2;
		final var KEY_CASE_ID = "CaseId";
		final var KEY_CREATED_BY = "CreatedBy";
		final var CREATED_BY = "createdBy";
		final var MODIFIED_BY = "modifiedBy";
		final var KEY_MODIFIED_BY = "ModifiedBy";

		return Stream.of(
			Arguments.of(NoteEntity.create().withCaseId(CASE_ID).withCreatedBy(CREATED_BY).withModifiedBy(MODIFIED_BY), null, null, Map.of(KEY_CASE_ID, CASE_ID, KEY_CREATED_BY, CREATED_BY, KEY_MODIFIED_BY, MODIFIED_BY)),
			Arguments.of(null, null, Revision.create().withId(PREVIOUS_ID).withVersion(PREVIOUS_VERSION), Map.of(KEY_PREVIOUS_REVISION, PREVIOUS_ID, KEY_PREVIOUS_VERSION, String.valueOf(PREVIOUS_VERSION))),
			Arguments.of(null, Revision.create().withId(CURRENT_ID).withVersion(CURRENT_VERSION), null, Map.of(KEY_CURRENT_REVISION, CURRENT_ID, KEY_CURRENT_VERSION, String.valueOf(CURRENT_VERSION))),
			Arguments.of(null, null, null, emptyMap()));
	}

}
