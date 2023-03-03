package se.sundsvall.notes.integration.db.specification;

import static java.util.Objects.nonNull;

import org.springframework.data.jpa.domain.Specification;

import se.sundsvall.notes.integration.db.model.NoteEntity;

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

	static Specification<NoteEntity> withCaseId(String caseId) {
		return buildEqualFilter("caseId", caseId);
	}

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