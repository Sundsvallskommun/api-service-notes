package se.sundsvall.notes.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.integration.db.model.NoteEntity;
import se.sundsvall.notes.integration.db.model.RevisionEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;

public class RevisionMapper {

	private static final Logger LOG = LoggerFactory.getLogger(RevisionMapper.class);
	private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

	private RevisionMapper() {
	}

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

	public static String toJsonString(final NoteEntity entity) {
		try {
			return objectMapper.writeValueAsString(entity);
		} catch (final JsonProcessingException e) {
			LOG.error("Error during serialization of entity into JSON string!", e);
		}

		return null;
	}

	public static RevisionEntity toRevisionEntity(NoteEntity entity, int version) {
		return RevisionEntity.create()
			.withEntityId(entity.getId())
			.withEntityType(entity.getClass().getSimpleName())
			.withVersion(version)
			.withSerializedSnapshot(toJsonString(entity));
	}
}
