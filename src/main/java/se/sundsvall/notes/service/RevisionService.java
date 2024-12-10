package se.sundsvall.notes.service;

import static com.flipkart.zjsonpatch.DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE;
import static com.flipkart.zjsonpatch.DiffFlags.OMIT_VALUE_ON_REMOVE;
import static java.lang.String.format;
import static org.apache.commons.lang3.ObjectUtils.anyNull;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.notes.service.ServiceConstants.PROBLEM_DURING_DIFF;
import static se.sundsvall.notes.service.ServiceConstants.REVISION_NOT_FOUND_FOR_ID_AND_VERSION;
import static se.sundsvall.notes.service.mapper.RevisionMapper.toRevision;
import static se.sundsvall.notes.service.mapper.RevisionMapper.toRevisionList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.notes.api.model.DifferenceResponse;
import se.sundsvall.notes.api.model.Operation;
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.integration.db.RevisionRepository;
import se.sundsvall.notes.integration.db.model.NoteEntity;
import se.sundsvall.notes.integration.db.model.RevisionEntity;

@Service
@Transactional
public class RevisionService {

	private static final Logger LOG = LoggerFactory.getLogger(RevisionService.class);
	private static final EnumSet<DiffFlags> DIFF_FLAGS = EnumSet.of(ADD_ORIGINAL_VALUE_ON_REPLACE, OMIT_VALUE_ON_REMOVE);

	private final RevisionRepository revisionRepository;
	private final ObjectMapper objectMapper;

	RevisionService(RevisionRepository revisionRepository, ObjectMapper objectMapper) {
		this.revisionRepository = revisionRepository;
		this.objectMapper = objectMapper;
	}

	/**
	 * Performs a diff between to versions of a NoteEntity.
	 * <p>
	 * The diff will be performed and returned according to RFC6902.
	 *
	 * @see                   <a href="https://datatracker.ietf.org/doc/html/rfc6902">RFC6902</a>.
	 * @param  noteEntityId   the NoteEntity id (uuid).
	 * @param  municipalityId the id of the municipality.
	 * @param  source         the diff source version.
	 * @param  target         the diff target version.
	 * @return                the difference result represented as a DifferenceResponse object.
	 */
	public DifferenceResponse diff(final String noteEntityId, final String municipalityId, final int source, final int target) {

		try {
			// Fetch revisions from DB.
			final var revisionEntity1 = revisionRepository.findByEntityIdAndMunicipalityIdAndVersion(noteEntityId, municipalityId, source)
				.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(REVISION_NOT_FOUND_FOR_ID_AND_VERSION, noteEntityId, source)));
			final var revisionEntity2 = revisionRepository.findByEntityIdAndMunicipalityIdAndVersion(noteEntityId, municipalityId, target)
				.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(REVISION_NOT_FOUND_FOR_ID_AND_VERSION, noteEntityId, target)));

			// Deserialize revision JSON into a JsonNode.
			final var sourceJson = objectMapper.readTree(revisionEntity1.getSerializedSnapshot());
			final var targetJson = objectMapper.readTree(revisionEntity2.getSerializedSnapshot());

			// Perform diff of the two JsonNodes.
			final var diffResult = JsonDiff.asJson(sourceJson, targetJson, DIFF_FLAGS).toString();

			// Return result.
			return DifferenceResponse.create().withOperations(List.of(objectMapper.readValue(diffResult, Operation[].class)));
		} catch (final IOException e) {
			LOG.error("Error occurred during diff: ", e);
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, format(PROBLEM_DURING_DIFF, noteEntityId, source, target));
		}
	}

	/**
	 * Create a new revision.
	 * <p>
	 * A new revision will be created if:
	 * - the last revisions serialized-snapshot differs from the current (i.e. provided) entity.
	 * - no previous revisions exist for the provided entity.
	 *
	 * @param  entity         the entity that will have a new revision.
	 * @param  municipalityId the id of the municipality.
	 * @return                the created revision.
	 */
	public Revision createRevision(final NoteEntity entity, final String municipalityId) {

		final var lastRevision = revisionRepository.findFirstByEntityIdAndMunicipalityIdOrderByVersionDesc(entity.getId(), municipalityId);

		if (lastRevision.isPresent()) {

			// No changes since last revision, return.
			if (jsonEquals(lastRevision.get().getSerializedSnapshot(), toJsonString(entity))) {
				return null;
			}

			// Create revision <lastRevision.version + 1>
			return toRevision(createRevision(entity, lastRevision.get().getVersion() + 1, municipalityId));
		}

		// No previous revisions exist. Create revision 0
		return toRevision(createRevision(entity, 0, municipalityId));
	}

	public List<Revision> getRevisions(final String noteEntityId, final String municipalityId) {
		return toRevisionList(revisionRepository.findAllByEntityIdAndMunicipalityIdOrderByVersionDesc(noteEntityId, municipalityId));
	}

	private RevisionEntity createRevision(final NoteEntity entity, final int version, final String municipalityId) {
		return revisionRepository.save(RevisionEntity.create()
			.withEntityId(entity.getId())
			.withEntityType(entity.getClass().getSimpleName())
			.withSerializedSnapshot(toJsonString(entity))
			.withVersion(version)
			.withMunicipalityId(municipalityId));
	}

	private boolean jsonEquals(final String json1, final String json2) {
		if (anyNull(json1, json2)) {
			return false;
		}

		try {
			return objectMapper.readTree(json1).equals(objectMapper.readTree(json2));
		} catch (final Exception e) {
			LOG.error("Error during JSON compare!", e);
			return false;
		}
	}

	private String toJsonString(final NoteEntity entity) {
		try {
			return objectMapper.writeValueAsString(entity);
		} catch (final JsonProcessingException e) {
			LOG.error("Error during serialization of entity into JSON string!", e);
		}

		return null;
	}
}
