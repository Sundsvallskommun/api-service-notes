package se.sundsvall.notes.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.integration.db.model.NoteEntity;
import se.sundsvall.notes.integration.db.model.RevisionEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.time.OffsetDateTime.now;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ExtendWith(MockitoExtension.class)
class RevisionMapperTest {

	@Test
	void toRevisionList() {

		// Arrange
		final var created = now();
		final var entityId = UUID.randomUUID().toString();
		final var entityType = NoteEntity.class.getSimpleName();
		final var id = UUID.randomUUID().toString();
		final var version = 1;

		final var revisionEntityList = List.of(
			RevisionEntity.create()
				.withCreated(created)
				.withEntityId(entityId)
				.withEntityType(entityType)
				.withId(id)
				.withVersion(version));

		// Act
		final var result = RevisionMapper.toRevisionList(revisionEntityList);

		// Assert
		assertThat(result)
			.hasSize(1)
			.extracting(
				Revision::getCreated,
				Revision::getEntityId,
				Revision::getEntityType,
				Revision::getId,
				Revision::getVersion)
			.containsExactly(
				tuple(created, entityId, entityType, id, version));
	}

	@Test
	void toRevisionListWithNullInput() {

		// Act
		final var result = RevisionMapper.toRevisionList(null);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toRevisionListWithEmptyInput() {

		// Act
		final var result = RevisionMapper.toRevisionList(emptyList());

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toRevisionListWithListContainingNulls() {

		// Act
		final var result = RevisionMapper.toRevisionList(Arrays.asList(RevisionEntity.create(), null, RevisionEntity.create()));

		// Assert
		assertThat(result).hasSize(2);
	}
}
