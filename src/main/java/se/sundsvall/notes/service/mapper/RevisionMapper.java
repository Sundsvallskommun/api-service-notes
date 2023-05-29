package se.sundsvall.notes.service.mapper;

import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.integration.db.model.RevisionEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;

public class RevisionMapper {

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

}
