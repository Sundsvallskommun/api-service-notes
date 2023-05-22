package se.sundsvall.notes.service.mapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.integration.db.model.NoteEntity;
import se.sundsvall.notes.integration.db.model.RevisionEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;

public class RevisionMapper {

	private RevisionMapper() {}

	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(OffsetDateTime.class, OffsetDateTimeSerializer.create())
		.create();

	public static List<Revision> toRevisionList(final List<RevisionEntity> revisionEntityList) {
		return Optional.ofNullable(revisionEntityList).orElse(emptyList()).stream()
			.map(RevisionMapper::toRevision)
			.filter(Objects::nonNull)
			.toList();
	}

	public static Revision toRevision(final RevisionEntity revisionEntity) {
		return Optional.ofNullable(revisionEntity)
			.map(entity -> Revision.create()
				.withCreated(entity.getCreated())
				.withEntityId(entity.getEntityId())
				.withEntityType(entity.getEntityType())
				.withId(entity.getId())
				.withVersion(entity.getVersion()))
			.orElse(null);
	}

	public static RevisionEntity toRevisionEntity(NoteEntity entity, int version) {
		return RevisionEntity.create()
			.withEntityId(entity.getId())
			.withEntityType(entity.getClass().getSimpleName())
			.withVersion(version)
			.withSerializedSnapshot(toSerializedSnapshot(entity));
	}

	public static String toSerializedSnapshot(NoteEntity entity) {
		return Optional.ofNullable(entity)
			.map(GSON::toJson)
			.orElse(null);
	}
}
