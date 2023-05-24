package se.sundsvall.notes.service.mapper;

import generated.se.sundsvall.eventlog.Event;
import generated.se.sundsvall.eventlog.EventType;
import generated.se.sundsvall.eventlog.Metadata;
import se.sundsvall.notes.api.model.Note;
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.integration.db.model.NoteEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.util.Collections.emptyMap;
import static java.util.Map.entry;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.allNull;

public class EventMapper {

	private static final String OWNER = "Notes";
	private static final String SOURCE_TYPE = Note.class.getSimpleName();
	private static final String KEY_CREATED_BY = "CreatedBy";
	private static final String KEY_MDOIFIED_BY = "ModifiedBy";
	private static final String KEY_EXECUTED_BY = "ExecutedBy";
	private static final String KEY_CASE_ID = "CaseId";
	private static final String KEY_PREVIOUS_REVISION = "PreviousRevision";
	private static final String KEY_PREVIOUS_VERSION = "PreviousVersion";
	private static final String KEY_CURRENT_REVISION = "CurrentRevision";
	private static final String KEY_CURRENT_VERSION = "CurrentVersion";

	private EventMapper() {}

	public static Event toEvent(EventType eventType, String message, String revision, Map<String, String> metaData, String executedByUserId) {
		if (allNull(eventType, message, revision, metaData, executedByUserId)) {
			return null;
		}

		return new Event()
			.created(now(systemDefault()))
			.historyReference(revision)
			.message(message)
			.owner(OWNER)
			.sourceType(SOURCE_TYPE)
			.type(eventType)
			.metadata(toMetadata(metaData, ofNullable(executedByUserId)));
	}

	public static Map<String, String> toMetadataMap(NoteEntity noteEntity, Revision currentRevision, Revision previousRevision) {
		final var metadata = new HashMap<String, String>();

		ofNullable(noteEntity).map(NoteEntity::getCaseId).ifPresent(caseId -> metadata.put(KEY_CASE_ID, caseId));

		// Add information about who created the note
		ofNullable(noteEntity).map(NoteEntity::getCreatedBy).ifPresent(createdBy -> metadata.put(KEY_CREATED_BY, createdBy));

		// Add information about who modified the note
		ofNullable(noteEntity).map(NoteEntity::getModifiedBy).ifPresent(modifiedBy -> metadata.put(KEY_MDOIFIED_BY, modifiedBy));

		// Add information for current revision of note
		ofNullable(currentRevision).ifPresent(rev -> {
			metadata.put(KEY_CURRENT_REVISION, rev.getId());
			metadata.put(KEY_CURRENT_VERSION, String.valueOf(rev.getVersion()));
		});

		// Add information for previous revision of note
		ofNullable(previousRevision).ifPresent(rev -> {
			metadata.put(KEY_PREVIOUS_REVISION, rev.getId());
			metadata.put(KEY_PREVIOUS_VERSION, String.valueOf(rev.getVersion()));
		});


		return metadata;
	}

	private static List<Metadata> toMetadata(Map<String, String> metadata, Optional<String> executedByUserId) {
		final var metadataList = new ArrayList<Metadata>();
		metadataList.addAll(toMetadatas(metadata));
		executedByUserId.ifPresent(userId -> metadataList.add(toMetadata(entry(KEY_EXECUTED_BY, userId))));

		return metadataList;
	}

	private static List<Metadata> toMetadatas(Map<String, String> metadata) {
		return ofNullable(metadata).orElse(emptyMap()).entrySet().stream()
			.map(EventMapper::toMetadata)
			.toList();
	}

	private static Metadata toMetadata(Map.Entry<String, String> entry) {
		return new Metadata().key(entry.getKey()).value(entry.getValue());
	}

}
