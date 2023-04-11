package se.sundsvall.notes.integration.db.specification;

import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.notes.integration.db.model.NoteEntity;

import static java.util.Objects.nonNull;

public interface NoteSpecification {

	static Specification<NoteEntity> withContext(String context) {
		return buildEqualFilter("context", context);
	}

	static Specification<NoteEntity> withRole(String role) {
		return buildEqualFilter("role", role);
	}

	static Specification<NoteEntity> withClientId(String clientId) {
		return buildEqualFilter("clientId", clientId);
	}

	static Specification<NoteEntity> withPartyId(String partyId) {
		return buildEqualFilter("partyId", partyId);
	}

	static Specification<NoteEntity> withCaseId(String caseId) { return buildEqualFilter("caseId", caseId); }

	static Specification<NoteEntity> withMunicipalityId(String municipalityId) { return buildEqualFilter("municipalityId", municipalityId); }

	/**
	 * Method builds an equal filter if value is not null. If value is null, method returns
	 * an always-true predicate (meaning no filtering will be applied for sent in attribute)
	 *
	 * @param attribute name that will be used in filter
	 * @param value     value (or null) to compare against
	 * @return {@code Specification<NoteEntity>} matching sent in comparison
	 */
	private static Specification<NoteEntity> buildEqualFilter(String attribute, Object value) {
		return (noteEntity, cq, cb) -> nonNull(value) ? cb.equal(noteEntity.get(attribute), value) : cb.and();
	}
}