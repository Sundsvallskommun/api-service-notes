package se.sundsvall.notes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.notes.api.model.CreateNoteRequest;
import se.sundsvall.notes.api.model.FindNotesRequest;
import se.sundsvall.notes.api.model.FindNotesResponse;
import se.sundsvall.notes.api.model.MetaData;
import se.sundsvall.notes.api.model.Note;
import se.sundsvall.notes.api.model.UpdateNoteRequest;
import se.sundsvall.notes.integration.db.NoteRepository;

import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.notes.service.ServiceConstants.ERROR_NOTE_NOT_FOUND;
import static se.sundsvall.notes.service.mapper.NoteMapper.toNote;
import static se.sundsvall.notes.service.mapper.NoteMapper.toNoteEntity;
import static se.sundsvall.notes.service.mapper.NoteMapper.toNotes;

@Service
public class NoteService {

	@Autowired
	private NoteRepository noteRepository;

	public String createNote(String municipalityId, CreateNoteRequest createNoteRequest) {
		return noteRepository.save(toNoteEntity(municipalityId, createNoteRequest)).getId();
	}

	public Note updateNote(String id, String municipalityId, UpdateNoteRequest updateNoteRequest) {
		if (isNotTrue(noteRepository.existsByIdAndMunicipalityId(id, municipalityId))) {
			throw Problem.valueOf(NOT_FOUND, format(ERROR_NOTE_NOT_FOUND, id));
		}

		final var noteEntity = noteRepository.save(toNoteEntity(municipalityId, noteRepository.getReferenceById(id), updateNoteRequest));
		return toNote(noteEntity);
	}

	public Note getNote(String id, String municipalityId) {
		if (isNotTrue(noteRepository.existsByIdAndMunicipalityId(id, municipalityId))) {
			throw Problem.valueOf(NOT_FOUND, format(ERROR_NOTE_NOT_FOUND, id));
		}

		return toNote(noteRepository.getReferenceById(id));
	}

	public FindNotesResponse getNotes(String municipalityId, FindNotesRequest findNotesRequest) {
		final var matches = noteRepository.findAllByParameters(municipalityId, findNotesRequest, PageRequest.of(findNotesRequest.getPage() - 1,
			findNotesRequest.getLimit(), Sort.by("created").descending()));

		// If page larger than last page is requested, a empty list is returned otherwise the current page
		List<Note> notes = matches.getTotalPages() < findNotesRequest.getPage() ? emptyList() : toNotes(matches.getContent());

		return FindNotesResponse.create()
			.withMetaData(MetaData.create()
				.withPage(findNotesRequest.getPage())
				.withTotalPages(matches.getTotalPages())
				.withTotalRecords(matches.getTotalElements())
				.withCount(notes.size())
				.withLimit(findNotesRequest.getLimit()))
			.withNotes(notes);
	}

	public void deleteNote(String id, String municipalityId) {
		if (isNotTrue(noteRepository.existsByIdAndMunicipalityId(id, municipalityId))) {
			throw Problem.valueOf(NOT_FOUND, format(ERROR_NOTE_NOT_FOUND, id));
		}
		noteRepository.deleteById(id);
	}
}
