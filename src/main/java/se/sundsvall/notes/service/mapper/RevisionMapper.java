package se.sundsvall.notes.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;

import java.util.List;
import java.util.Optional;

import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.integration.db.model.RevisionEntity;

public class RevisionMapper {

	private RevisionMapper() {}

	public static List<Revision> toRevisionList(final List<RevisionEntity> revisionEntityList) {
		return Optional.ofNullable(revisionEntityList).orElse(emptyList()).stream()
			.map(RevisionMapper::toRevision)
			.toList();
	}

	private static Revision toRevision(final RevisionEntity revisionEntity) {
		if (isNull(revisionEntity)) {
			return null;
		}

		return Revision.create()
			.withCreated(revisionEntity.getCreated())
			.withEntityId(revisionEntity.getEntityId())
			.withEntityType(RevisionEntity.class.getSimpleName())
			.withId(revisionEntity.getId())
			.withVersion(revisionEntity.getVersion());
	}
}
