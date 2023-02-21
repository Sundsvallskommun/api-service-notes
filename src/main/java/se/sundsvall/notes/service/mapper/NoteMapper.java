package se.sundsvall.notes.service.mapper;

import se.sundsvall.notes.api.model.CreateNoteRequest;
import se.sundsvall.notes.api.model.Note;
import se.sundsvall.notes.api.model.UpdateNoteRequest;
import se.sundsvall.notes.integration.db.model.NoteEntity;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

public class NoteMapper {

	private NoteMapper() {
	}

	public static NoteEntity toNoteEntity(CreateNoteRequest createNoteRequest) {
		if (isNull(createNoteRequest)) {
			return null;
		}

		return NoteEntity.create()
			.withContext(createNoteRequest.getContext())
			.withRole(createNoteRequest.getRole())
			.withClientId(createNoteRequest.getClientId())
			.withBody(createNoteRequest.getBody())
			.withCreatedBy(createNoteRequest.getCreatedBy())
			.withPartyId(createNoteRequest.getPartyId())
			.withSubject(createNoteRequest.getSubject())
			.withCaseId(createNoteRequest.getCaseId())
			.withCaseType(createNoteRequest.getCaseType())
			.withCaseLink(createNoteRequest.getCaseLink())
			.withExternalCaseId(createNoteRequest.getExternalCaseId());
	}

	public static NoteEntity toNoteEntity(NoteEntity noteEntity, UpdateNoteRequest updateNoteRequest) {
		if (isNull(updateNoteRequest)) {
			return noteEntity;
		}
		ofNullable(updateNoteRequest.getBody()).ifPresent(noteEntity::setBody);
		ofNullable(updateNoteRequest.getModifiedBy()).ifPresent(noteEntity::setModifiedBy);
		ofNullable(updateNoteRequest.getSubject()).ifPresent(noteEntity::setSubject);
		ofNullable(updateNoteRequest.getCaseId()).ifPresent(noteEntity::setCaseId);
		ofNullable(updateNoteRequest.getCaseType()).ifPresent(noteEntity::setCaseType);
		ofNullable(updateNoteRequest.getCaseLink()).ifPresent(noteEntity::setCaseLink);
		ofNullable(updateNoteRequest.getExternalCaseId()).ifPresent(noteEntity::setExternalCaseId);

		return noteEntity;
	}

	public static Note toNote(NoteEntity noteEntity) {
		if (isNull(noteEntity)) {
			return null;
		}

		return Note.create()
			.withContext(noteEntity.getContext())
			.withRole(noteEntity.getRole())
			.withClientId(noteEntity.getClientId())
			.withBody(noteEntity.getBody())
			.withCreated(noteEntity.getCreated())
			.withCreatedBy(noteEntity.getCreatedBy())
			.withId(noteEntity.getId())
			.withModified(noteEntity.getModified())
			.withModifiedBy(noteEntity.getModifiedBy())
			.withPartyId(noteEntity.getPartyId())
			.withSubject(noteEntity.getSubject())
			.withCaseId(noteEntity.getCaseId())
			.withCaseType(noteEntity.getCaseType())
			.withCaseLink(noteEntity.getCaseLink())
			.withExternalCaseId(noteEntity.getExternalCaseId());
	}

	public static List<Note> toNotes(List<NoteEntity> noteEntities) {
		return ofNullable(noteEntities).orElse(emptyList()).stream()
			.map(NoteMapper::toNote)
			.toList();
	}
}
