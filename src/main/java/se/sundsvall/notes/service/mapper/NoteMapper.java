package se.sundsvall.notes.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import se.sundsvall.notes.api.model.CreateNoteRequest;
import se.sundsvall.notes.api.model.Note;
import se.sundsvall.notes.api.model.UpdateNoteRequest;
import se.sundsvall.notes.integration.db.model.NoteEntity;

public final class NoteMapper {

	private NoteMapper() {}

	public static NoteEntity toNoteEntity(final String municipalityId, final CreateNoteRequest request) {
		return Optional.ofNullable(request)
			.map(r -> NoteEntity.create()
				.withContext(r.getContext())
				.withRole(r.getRole())
				.withClientId(r.getClientId())
				.withBody(r.getBody())
				.withCreatedBy(r.getCreatedBy())
				.withPartyId(r.getPartyId())
				.withSubject(r.getSubject())
				.withCaseId(r.getCaseId())
				.withCaseType(r.getCaseType())
				.withCaseLink(r.getCaseLink())
				.withExternalCaseId(r.getExternalCaseId())
				.withMunicipalityId(municipalityId))
			.orElse(null);
	}

	public static NoteEntity toNoteEntity(final NoteEntity noteEntity, final UpdateNoteRequest updateNoteRequest) {
		return Optional.ofNullable(updateNoteRequest)
			.map(request -> {
				ofNullable(request.getBody()).ifPresent(noteEntity::setBody);
				ofNullable(request.getModifiedBy()).ifPresent(noteEntity::setModifiedBy);
				ofNullable(request.getSubject()).ifPresent(noteEntity::setSubject);
				ofNullable(request.getCaseId()).ifPresent(noteEntity::setCaseId);
				ofNullable(request.getCaseType()).ifPresent(noteEntity::setCaseType);
				ofNullable(request.getCaseLink()).ifPresent(noteEntity::setCaseLink);
				ofNullable(request.getExternalCaseId()).ifPresent(noteEntity::setExternalCaseId);
				return noteEntity;
			})
			.orElse(noteEntity);
	}

	public static Note toNote(final NoteEntity entity) {
		return Optional.ofNullable(entity)
			.map(e -> Note.create()
				.withContext(e.getContext())
				.withRole(e.getRole())
				.withClientId(e.getClientId())
				.withBody(e.getBody())
				.withCreated(e.getCreated())
				.withCreatedBy(e.getCreatedBy())
				.withId(e.getId())
				.withModified(e.getModified())
				.withModifiedBy(e.getModifiedBy())
				.withPartyId(e.getPartyId())
				.withSubject(e.getSubject())
				.withCaseId(e.getCaseId())
				.withCaseType(e.getCaseType())
				.withCaseLink(e.getCaseLink())
				.withExternalCaseId(e.getExternalCaseId()))
			.orElse(null);
	}

	public static List<Note> toNotes(final List<NoteEntity> noteEntities) {
		return ofNullable(noteEntities).orElse(emptyList()).stream()
			.map(NoteMapper::toNote)
			.filter(Objects::nonNull)
			.toList();
	}
}
