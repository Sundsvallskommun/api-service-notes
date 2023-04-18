package se.sundsvall.notes.integration.db;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.notes.integration.db.model.RevisionEntity;

@Transactional
@CircuitBreaker(name = "RevisionRepository")
public interface RevisionRepository extends JpaRepository<RevisionEntity, String> {

	/**
	 * Find the revision by entityId and entityType.
	 *
	 * @param entityId the entityId for the revision.
	 * @param version  the version for the revision.
	 * @return an optional entity that matches the provided parameters.
	 */
	Optional<RevisionEntity> findByEntityIdAndVersion(String entityId, int version);

	/**
	 * Find the last revision by entityId.
	 *
	 * @param entityId the entityId to find revisions for.
	 * @return an optional entity that matches the provided parameters (i.e. last created revision for an entity).
	 */
	Optional<RevisionEntity> findFirstByEntityIdOrderByVersionDesc(String entityId);
}
