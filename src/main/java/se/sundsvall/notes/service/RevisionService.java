package se.sundsvall.notes.service;

import static org.apache.commons.lang3.ObjectUtils.anyNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import se.sundsvall.notes.integration.db.RevisionRepository;
import se.sundsvall.notes.integration.db.model.NoteEntity;
import se.sundsvall.notes.integration.db.model.RevisionEntity;

@Service
public class RevisionService {

	private static final Logger LOG = LoggerFactory.getLogger(RevisionService.class);

	private final ObjectMapper objectMapper;

	@Autowired
	private RevisionRepository revisionRepository;

	public RevisionService() {
		objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
	}

	/**
	 * Create a new revision.
	 *
	 * A new revision will be created if:
	 * - the last revisions serialized-snapshot differs from the current (i.e. provided) entity.
	 * - no previous revisions exist for the provided entity.
	 *
	 * @param entity the entity that will have a new revision.
	 * @return the id (uuid) of the created revision.
	 */
	public String createRevision(final NoteEntity entity) {

		final var lastRevision = revisionRepository.findFirstByEntityIdOrderByVersionDesc(entity.getId());

		if (lastRevision.isPresent()) {

			// No changes since last revision, return.
			if (jsonEquals(lastRevision.get().getSerializedSnapshot(), toJsonString(entity))) {
				return null;
			}

			// Create revision <lastRevision.version + 1>
			return createRevision(entity, lastRevision.get().getVersion() + 1);
		}

		// No previous revisions exist. Create revision 0
		return createRevision(entity, 0);
	}

	private String toJsonString(final NoteEntity entity) {
		try {
			return objectMapper.writeValueAsString(entity);
		} catch (final JsonProcessingException e) {
			LOG.error("Error during serialization of entity into Json string", e);
		}

		return null;
	}

	private String createRevision(final NoteEntity entity, final int version) {
		return revisionRepository.save(RevisionEntity.create()
			.withEntityId(entity.getId())
			.withEntityType(entity.getClass().getSimpleName())
			.withSerializedSnapshot(toJsonString(entity))
			.withVersion(version)).getId();
	}

	private boolean jsonEquals(final String json1, final String json2) {
		if (anyNull(json1, json2)) {
			return false;
		}

		try {
			return objectMapper.readTree(json1).equals(objectMapper.readTree(json2));
		} catch (final Exception e) {
			LOG.error("Error during json compare", e);
			return false;
		}
	}
}
