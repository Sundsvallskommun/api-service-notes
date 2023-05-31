package se.sundsvall.notes.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.notes.api.model.DifferenceResponse;
import se.sundsvall.notes.api.model.Operation;
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.integration.db.RevisionRepository;
import se.sundsvall.notes.integration.db.model.NoteEntity;
import se.sundsvall.notes.integration.db.model.RevisionEntity;
import se.sundsvall.notes.service.mapper.RevisionMapper;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.flipkart.zjsonpatch.DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE;
import static com.flipkart.zjsonpatch.DiffFlags.OMIT_VALUE_ON_REMOVE;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.anyNull;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.notes.service.ServiceConstants.PROBLEM_DURING_DIFF;
import static se.sundsvall.notes.service.ServiceConstants.REVISION_NOT_FOUND_FOR_ID_AND_VERSION;
import static se.sundsvall.notes.service.mapper.RevisionMapper.toRevisionList;

@Service
@Transactional
public class RevisionService {

	private static final Logger LOG = LoggerFactory.getLogger(RevisionService.class);
	private static final EnumSet<DiffFlags> DIFF_FLAGS = EnumSet.of(ADD_ORIGINAL_VALUE_ON_REPLACE, OMIT_VALUE_ON_REMOVE);

	private static final String KEY_CURRENT_REVISION = "x-current-revision";
	private static final String KEY_CURRENT_VERSION = "x-current-version";
	private static final String KEY_PREVIOUS_VERSION = "x-previous-version";
	private static final String KEY_PREVIOUS_REVISION = "x-previous-revision";

	@Autowired
	private RevisionRepository revisionRepository;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Performs a diff between to versions of a NoteEtity.
	 *
	 * The diff will be performed and returned according to RFC6902.
	 *
	 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6902">RFC6902</a>.
	 * @param noteEntityId the NoteEntity id (uuid).
	 * @param source       the diff source version.
	 * @param target       the diff target version.
	 * @return the difference result represented as a DifferenceResponse object.
	 */
	public DifferenceResponse diff(final String noteEntityId, final int source, final int target) {

		try {
			// Fetch revisions from DB.
			final var revisionEntity1 = revisionRepository.findByEntityIdAndVersion(noteEntityId, source)
				.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(REVISION_NOT_FOUND_FOR_ID_AND_VERSION, noteEntityId, source)));
			final var revisionEntity2 = revisionRepository.findByEntityIdAndVersion(noteEntityId, target)
				.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(REVISION_NOT_FOUND_FOR_ID_AND_VERSION, noteEntityId, target)));

			// Deserialize revision JSON into a JsonNode.
			final var sourceJson = objectMapper.readTree(revisionEntity1.getSerializedSnapshot());
			final var targetJson = objectMapper.readTree(revisionEntity2.getSerializedSnapshot());

			// Perform diff of the two JsonNodes.
			final var diffResult = JsonDiff.asJson(sourceJson, targetJson, DIFF_FLAGS).toString();

			// Return result.
			return DifferenceResponse.create().withOperations(List.of(objectMapper.readValue(diffResult, Operation[].class)));
		} catch (final IOException e) {
			LOG.error("Error occured during diff: ", e);
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, format(PROBLEM_DURING_DIFF, noteEntityId, source, target));
		}
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

	public List<Revision> getRevisions(final String noteEntityId) {
		return toRevisionList(revisionRepository.findAllByEntityIdOrderByVersion(noteEntityId));
	}

	public Map<String, String> getRevisionHeaders(final String noteEntityId, HttpMethod method) {
		final var latestRevision = getLatestNoteRevision(noteEntityId);
		final var previousRevision = ofNullable(latestRevision)
			.filter(rev -> HttpMethod.PATCH.equals(method)) // We don't fetch previous version for POST (create) and DELETE (delete)
			.map(rev -> getNoteRevisionByVersion(noteEntityId, rev.getVersion() - 1))
			.orElse(null);

		return createRevisionInfoMap(latestRevision, previousRevision);
	}

	private String createRevision(final NoteEntity entity, final int version) {
		return revisionRepository.save(RevisionEntity.create()
			.withEntityId(entity.getId())
			.withEntityType(entity.getClass().getSimpleName())
			.withSerializedSnapshot(toJsonString(entity))
			.withVersion(version)).getId();
	}

	public boolean jsonEquals(final String json1, final String json2) {
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

	private Revision getLatestNoteRevision(String noteId) {
		return revisionRepository.findFirstByEntityIdOrderByVersionDesc(noteId)
			.map(RevisionMapper::toRevision)
			.orElse(null);
	}

	private Revision getNoteRevisionByVersion(String noteId, int version) {
		return revisionRepository.findByEntityIdAndVersion(noteId, version)
			.map(RevisionMapper::toRevision)
			.orElse(null);
	}

	private Map<String, String> createRevisionInfoMap(Revision currentRevision, Revision previousRevision) {

		final var metadata = new HashMap<String, String>();

		// Add information for current revision of note
		ofNullable(currentRevision).ifPresent(rev -> {
			metadata.put(KEY_CURRENT_REVISION, rev.getId());
			metadata.put(KEY_CURRENT_VERSION, String.valueOf(rev.getVersion()));
		});

		// Add information for previous revision of errand
		ofNullable(previousRevision).ifPresent(rev -> {
			metadata.put(KEY_PREVIOUS_REVISION, rev.getId());
			metadata.put(KEY_PREVIOUS_VERSION, String.valueOf(rev.getVersion()));
		});

		return metadata;
	}
}
