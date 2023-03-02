package se.sundsvall.notes.integration.db;

import static se.sundsvall.notes.integration.db.specification.NoteSpecification.withCaseId;
import static se.sundsvall.notes.integration.db.specification.NoteSpecification.withClientId;
import static se.sundsvall.notes.integration.db.specification.NoteSpecification.withContext;
import static se.sundsvall.notes.integration.db.specification.NoteSpecification.withPartyId;
import static se.sundsvall.notes.integration.db.specification.NoteSpecification.withRole;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.notes.api.model.FindNotesRequest;
import se.sundsvall.notes.integration.db.model.NoteEntity;

@Transactional
@CircuitBreaker(name = "NoteRepository")
public interface NoteRepository extends JpaRepository<NoteEntity, String>, JpaSpecificationExecutor<NoteEntity> {

	default Page<NoteEntity> findAllByParameters(FindNotesRequest findNotesRequest, Pageable pageable) {
		return this.findAll(withPartyId(findNotesRequest.getPartyId())
			.and(withCaseId(findNotesRequest.getCaseId()))
			.and(withContext(findNotesRequest.getContext())
			.and(withRole(findNotesRequest.getRole()))
			.and(withClientId(findNotesRequest.getClientId()))), pageable);
	}
}
