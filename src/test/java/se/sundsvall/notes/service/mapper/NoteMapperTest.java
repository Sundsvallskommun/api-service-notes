package se.sundsvall.notes.service.mapper;

import org.junit.jupiter.api.Test;
import se.sundsvall.notes.api.model.CreateNoteRequest;
import se.sundsvall.notes.api.model.Note;
import se.sundsvall.notes.api.model.UpdateNoteRequest;
import se.sundsvall.notes.integration.db.model.NoteEntity;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class NoteMapperTest {

	@Test
	void toNoteEntityFromCreateNoteRequest() {

		final var body = "body";
		final var context = "context";
		final var role = "role";
		final var clientId = "clientId";
		final var createdBy = "createdBy";
		final var partyId = "partyId";
		final var subject = "subject";
		final var caseId = "caseId";
		final var caseType = "caseType";
		final var caseLink = "caseLink";
		final var externalCaseId = "externalCaseId";
		final var municipalityId = "municipalityId";

		// Setup
		final var createNoteRequest = CreateNoteRequest.create()
			.withBody(body)
			.withContext(context)
			.withRole(role)
			.withClientId(clientId)
			.withCreatedBy(createdBy)
			.withPartyId(partyId)
			.withSubject(subject)
			.withCaseId(caseId)
			.withCaseType(caseType)
			.withCaseLink(caseLink)
			.withExternalCaseId(externalCaseId);

		// Call
		final var noteEntity = NoteMapper.toNoteEntity(municipalityId, createNoteRequest);

		// Verification
		assertThat(noteEntity.getBody()).isEqualTo(body);
		assertThat(noteEntity.getContext()).isEqualTo(context);
		assertThat(noteEntity.getRole()).isEqualTo(role);
		assertThat(noteEntity.getClientId()).isEqualTo(clientId);
		assertThat(noteEntity.getCreatedBy()).isEqualTo(createdBy);
		assertThat(noteEntity.getPartyId()).isEqualTo(partyId);
		assertThat(noteEntity.getSubject()).isEqualTo(subject);
		assertThat(noteEntity.getCaseId()).isEqualTo(caseId);
		assertThat(noteEntity.getCaseType()).isEqualTo(caseType);
		assertThat(noteEntity.getCaseLink()).isEqualTo(caseLink);
		assertThat(noteEntity.getExternalCaseId()).isEqualTo(externalCaseId);
		assertThat(noteEntity.getMunicipalityId()).isEqualTo(municipalityId);

		assertThat(noteEntity).extracting(
			NoteEntity::getCreated,
			NoteEntity::getId,
			NoteEntity::getModified,
			NoteEntity::getModifiedBy).containsOnlyNulls();
	}

	@Test
	void toNoteEntityFromNullRequest() {
		// Setup
		final var municipalityId = "municipalityId";
		// Call
		final var webMessageEntity = NoteMapper.toNoteEntity(municipalityId, null);

		// Verification
		assertThat(webMessageEntity).isNull();
	}

	@Test
	void updateNoteEntityFromUpdateNoteRequest() {

		final var body = "body";
		final var context = "context";
		final var role = "role";
		final var clientId = "clientId";
		final var created = OffsetDateTime.now();
		final var createdBy = "createdBy";
		final var id = "id";
		final var partyId = "partyId";
		final var subject = "subject";
		final var modifiedBy = "modifiedBy";
		final var caseId = "caseId";
		final var caseType = "caseType";
		final var caseLink = "caseLink";
		final var externalCaseId = "externalCaseId";

		// Setup
		final var noteEntity = NoteEntity.create()
			.withBody(body)
			.withContext(context)
			.withRole(role)
			.withClientId(clientId)
			.withCreated(created)
			.withCreatedBy(createdBy)
			.withId(id)
			.withPartyId(partyId)
			.withSubject(subject)
			.withCaseId(caseId)
			.withCaseType(caseType)
			.withCaseLink(caseLink)
			.withExternalCaseId(externalCaseId);

		final var updateNoteRequest = UpdateNoteRequest.create()
			.withBody(body.concat("updated"))
			.withModifiedBy(modifiedBy)
			.withSubject(subject.concat("updated"))
			.withCaseId(caseId.concat("updated"))
			.withCaseType(caseType.concat("updated"))
			.withCaseLink(caseLink.concat("updated"))
			.withExternalCaseId(externalCaseId.concat("updated"));

		// Call
		final var updatedNoteEntity = NoteMapper.toNoteEntity(noteEntity, updateNoteRequest);

		// Verification
		assertThat(updatedNoteEntity.getId()).isEqualTo(id);
		assertThat(updatedNoteEntity.getBody()).isEqualTo(body.concat("updated"));
		assertThat(updatedNoteEntity.getContext()).isEqualTo(context);
		assertThat(updatedNoteEntity.getRole()).isEqualTo(role);
		assertThat(updatedNoteEntity.getClientId()).isEqualTo(clientId);
		assertThat(updatedNoteEntity.getCreated()).isEqualTo(created);
		assertThat(updatedNoteEntity.getCreatedBy()).isEqualTo(createdBy);
		assertThat(updatedNoteEntity.getPartyId()).isEqualTo(partyId);
		assertThat(updatedNoteEntity.getSubject()).isEqualTo(subject.concat("updated"));
		assertThat(updatedNoteEntity.getModified()).isNull();
		assertThat(updatedNoteEntity.getModifiedBy()).isEqualTo(modifiedBy);
		assertThat(updatedNoteEntity.getCaseId()).isEqualTo(caseId.concat("updated"));
		assertThat(updatedNoteEntity.getCaseType()).isEqualTo(caseType.concat("updated"));
		assertThat(updatedNoteEntity.getCaseLink()).isEqualTo(caseLink.concat("updated"));
		assertThat(updatedNoteEntity.getExternalCaseId()).isEqualTo(externalCaseId.concat("updated"));
	}

	@Test
	void updateNoteEntityFromUpdateNoteRequestOnlyModifiedBySet() {

		final var body = "body";
		final var context = "context";
		final var role = "role";
		final var clientId = "clientId";
		final var created = OffsetDateTime.now();
		final var createdBy = "createdBy";
		final var id = "id";
		final var partyId = "partyId";
		final var subject = "subject";
		final var modifiedBy = "modifiedBy";
		final var caseId = "caseId";
		final var caseType = "caseType";
		final var caseLink = "caseLink";
		final var externalCaseId = "externalCaseId";

		// Setup
		final var noteEntity = NoteEntity.create()
			.withBody(body)
			.withContext(context)
			.withRole(role)
			.withClientId(clientId)
			.withCreated(created)
			.withCreatedBy(createdBy)
			.withId(id)
			.withPartyId(partyId)
			.withSubject(subject)
			.withCaseId(caseId)
			.withCaseType(caseType)
			.withCaseLink(caseLink)
			.withExternalCaseId(externalCaseId);

		final var updateNoteRequest = UpdateNoteRequest.create()
			.withModifiedBy(modifiedBy);

		// Call
		final var updatedNoteEntity = NoteMapper.toNoteEntity(noteEntity, updateNoteRequest);

		// Verification
		assertThat(updatedNoteEntity.getId()).isEqualTo(id);
		assertThat(updatedNoteEntity.getBody()).isEqualTo(body);
		assertThat(updatedNoteEntity.getContext()).isEqualTo(context);
		assertThat(updatedNoteEntity.getRole()).isEqualTo(role);
		assertThat(updatedNoteEntity.getClientId()).isEqualTo(clientId);
		assertThat(updatedNoteEntity.getCreated()).isEqualTo(created);
		assertThat(updatedNoteEntity.getCreatedBy()).isEqualTo(createdBy);
		assertThat(updatedNoteEntity.getPartyId()).isEqualTo(partyId);
		assertThat(updatedNoteEntity.getSubject()).isEqualTo(subject);
		assertThat(updatedNoteEntity.getModified()).isNull();
		assertThat(updatedNoteEntity.getModifiedBy()).isEqualTo(modifiedBy);
		assertThat(updatedNoteEntity.getCaseId()).isEqualTo(caseId);
		assertThat(updatedNoteEntity.getCaseType()).isEqualTo(caseType);
		assertThat(updatedNoteEntity.getCaseLink()).isEqualTo(caseLink);
		assertThat(updatedNoteEntity.getExternalCaseId()).isEqualTo(externalCaseId);
	}

	@Test
	void updateNoteEntityFromNull() {

		final var body = "body";
		final var context = "context";
		final var role = "role";
		final var clientId = "clientId";
		final var created = OffsetDateTime.now();
		final var createdBy = "createdBy";
		final var id = "id";
		final var partyId = "partyId";
		final var subject = "subject";
		final var caseId = "caseId";
		final var caseType = "caseType";
		final var caseLink = "caseLink";
		final var externalCaseId = "externalCaseId";
		final var municipalityId = "municipalityId";

		// Setup
		final var noteEntity = NoteEntity.create()
			.withBody(body)
			.withContext(context)
			.withRole(role)
			.withClientId(clientId)
			.withCreated(created)
			.withCreatedBy(createdBy)
			.withId(id)
			.withPartyId(partyId)
			.withSubject(subject)
			.withCaseId(caseId)
			.withCaseType(caseType)
			.withCaseLink(caseLink)
			.withExternalCaseId(externalCaseId)
			.withMunicipalityId(municipalityId);

		// Call
		final var updatedNoteEntity = NoteMapper.toNoteEntity(noteEntity, null);

		// Verification
		assertThat(updatedNoteEntity.getId()).isEqualTo(id);
		assertThat(updatedNoteEntity.getBody()).isEqualTo(body);
		assertThat(updatedNoteEntity.getContext()).isEqualTo(context);
		assertThat(updatedNoteEntity.getRole()).isEqualTo(role);
		assertThat(updatedNoteEntity.getClientId()).isEqualTo(clientId);
		assertThat(updatedNoteEntity.getCreated()).isEqualTo(created);
		assertThat(updatedNoteEntity.getCreatedBy()).isEqualTo(createdBy);
		assertThat(updatedNoteEntity.getPartyId()).isEqualTo(partyId);
		assertThat(updatedNoteEntity.getSubject()).isEqualTo(subject);
		assertThat(updatedNoteEntity.getModified()).isNull();
		assertThat(updatedNoteEntity.getModifiedBy()).isNull();
		assertThat(updatedNoteEntity.getCaseId()).isEqualTo(caseId);
		assertThat(updatedNoteEntity.getCaseType()).isEqualTo(caseType);
		assertThat(updatedNoteEntity.getCaseLink()).isEqualTo(caseLink);
		assertThat(updatedNoteEntity.getExternalCaseId()).isEqualTo(externalCaseId);
		assertThat(updatedNoteEntity.getMunicipalityId()).isEqualTo(municipalityId);
	}

	@Test
	void toNote() {
		final var body = "body";
		final var context = "context";
		final var role = "role";
		final var clientId = "clientId";
		final var created = OffsetDateTime.now().minusDays(7L);
		final var createdBy = "createdBy";
		final var id = "id";
		final var modified = OffsetDateTime.now();
		final var modifiedBy = "modifiedBy";
		final var partyId = "partyId";
		final var subject = "subject";
		final var caseId = "caseId";
		final var caseType = "caseType";
		final var caseLink = "caseLink";
		final var externalCaseId = "externalCaseId";

		// Setup
		final var noteEntity = NoteEntity.create()
			.withBody(body)
			.withContext(context)
			.withRole(role)
			.withClientId(clientId)
			.withCreated(created)
			.withCreatedBy(createdBy)
			.withId(id)
			.withPartyId(partyId)
			.withSubject(subject)
			.withModified(modified)
			.withModifiedBy(modifiedBy)
			.withCaseId(caseId)
			.withCaseType(caseType)
			.withCaseLink(caseLink)
			.withExternalCaseId(externalCaseId);

		// Call
		final var note = NoteMapper.toNote(noteEntity);

		// Verification
		assertThat(note.getBody()).isEqualTo(body);
		assertThat(note.getContext()).isEqualTo(context);
		assertThat(note.getRole()).isEqualTo(role);
		assertThat(note.getClientId()).isEqualTo(clientId);
		assertThat(note.getCreated()).isEqualTo(created);
		assertThat(note.getCreatedBy()).isEqualTo(createdBy);
		assertThat(note.getId()).isEqualTo(id);
		assertThat(note.getModified()).isEqualTo(modified);
		assertThat(note.getModifiedBy()).isEqualTo(modifiedBy);
		assertThat(note.getPartyId()).isEqualTo(partyId);
		assertThat(note.getSubject()).isEqualTo(subject);
		assertThat(note.getCaseId()).isEqualTo(caseId);
		assertThat(note.getCaseType()).isEqualTo(caseType);
		assertThat(note.getCaseLink()).isEqualTo(caseLink);
		assertThat(note.getExternalCaseId()).isEqualTo(externalCaseId);
	}

	@Test
	void toNoteFromNull() {

		// Call
		final var note = NoteMapper.toNote(null);

		// Verification
		assertThat(note).isNull();
	}

	@Test
	void toNotes() {
		final var body = "body";
		final var context = "context";
		final var role = "role";
		final var clientId = "clientId";
		final var created = OffsetDateTime.now().minusDays(7L);
		final var createdBy = "createdBy";
		final var id = "id";
		final var modified = OffsetDateTime.now();
		final var modifiedBy = "modifiedBy";
		final var partyId = "partyId";
		final var subject = "subject";
		final var caseId = "caseId";
		final var caseType = "caseType";
		final var caseLink = "caseLink";
		final var externalCaseId = "externalCaseId";

		// Setup
		final var noteEntity = NoteEntity.create()
			.withBody(body)
			.withContext(context)
			.withRole(role)
			.withClientId(clientId)
			.withCreated(created)
			.withCreatedBy(createdBy)
			.withId(id)
			.withPartyId(partyId)
			.withSubject(subject)
			.withModified(modified)
			.withModifiedBy(modifiedBy)
			.withCaseId(caseId)
			.withCaseType(caseType)
			.withCaseLink(caseLink)
			.withExternalCaseId(externalCaseId);

		// Call
		final var notes = NoteMapper.toNotes(List.of(noteEntity));

		// Verification
		assertThat(notes).hasSize(1).extracting(
			Note::getBody,
			Note::getContext,
			Note::getRole,
			Note::getClientId,
			Note::getCreated,
			Note::getCreatedBy,
			Note::getId,
			Note::getModified,
			Note::getModifiedBy,
			Note::getPartyId,
			Note::getSubject,
			Note::getCaseId,
			Note::getCaseType,
			Note::getCaseLink,
			Note::getExternalCaseId).containsExactly(
			tuple(body, context, role, clientId, created, createdBy, id, modified, modifiedBy, partyId, subject, caseId, caseType, caseLink, externalCaseId));
	}

	@Test
	void toNotesFromNull() {

		// Call
		final var notes = NoteMapper.toNotes(null);

		// Verification
		assertThat(notes).isEmpty();
	}
}
