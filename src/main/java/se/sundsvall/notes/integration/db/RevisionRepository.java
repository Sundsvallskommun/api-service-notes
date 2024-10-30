package se.sundsvall.notes.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.notes.integration.db.model.RevisionEntity;

import java.util.List;
import java.util.Optional;

@CircuitBreaker(name = "RevisionRepository")
public interface RevisionRepository extends JpaRepository<RevisionEntity, String> {

	/**
	 * Find the last revision by entityId.
	 *
	 * @param  entityId       the entityId to find revisions for.
	 * @param  municipalityId the municipalityId to find revisions for.
	 * @return                an optional entity that matches the provided parameters (i.e. last created revision for an
	 *                        entity).
	 */
	Optional<RevisionEntity> findFirstByEntityIdAndMunicipalityIdOrderByVersionDesc(String entityId, String municipalityId);

	/**
	 * Find the revision by entityId and version.
	 *
	 * @param  entityId       the entityId for the revision.
	 * @param  municipalityId the municipalityId for the revision.
	 * @param  version        the version for the revision.
	 * @return                an optional entity that matches the provided parameters.
	 */
	Optional<RevisionEntity> findByEntityIdAndMunicipalityIdAndVersion(String entityId, String municipalityId, int version);

	/**
	 * Find all revisions by entityId and municipalityId.
	 *
	 * @param  entityId       the entityId.
	 * @param  municipalityId the id of municipality.
	 * @return                a list of entities that matches the provided parameter ordered by version descending.
	 */
	List<RevisionEntity> findAllByEntityIdAndMunicipalityIdOrderByVersionDesc(String entityId, String municipalityId);
}
