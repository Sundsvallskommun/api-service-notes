package se.sundsvall.notes.integration.db;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.notes.integration.db.model.RevisionEntity;

@CircuitBreaker(name = "RevisionRepository")
public interface RevisionRepository extends JpaRepository<RevisionEntity, String> {

	/**
	 * Find the last revision by entityId.
	 *
	 * @param entityId the entityId to find revisions for.
	 * @return an optional entity that matches the provided parameters (i.e. last created revision for an entity).
	 */
	Optional<RevisionEntity> findFirstByEntityIdOrderByVersionDesc(String entityId);

	/**
	 * Find the revision by entityId and version.
	 *
	 * @param entityId the entityId for the revision.
	 * @param version  the version for the revision.
	 * @return an optional entity that matches the provided parameters.
	 */
	Optional<RevisionEntity> findByEntityIdAndVersion(String entityId, int version);

	/**
	 * Find all revisions by entityId.
	 *
	 * @param entityId the entityId.
	 * @return a list of entities that matches the provided parameter ordered by version descending.
	 */
	List<RevisionEntity> findAllByEntityIdOrderByVersionDesc(String entityId);
}
