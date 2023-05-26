package se.sundsvall.notes.service;

import jakarta.transaction.Transactional;
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
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.notes.service.ServiceConstants.ERROR_NOTE_NOT_FOUND;
import static se.sundsvall.notes.service.mapper.NoteMapper.toNote;
import static se.sundsvall.notes.service.mapper.NoteMapper.toNoteEntity;
import static se.sundsvall.notes.service.mapper.NoteMapper.toNotes;

@Service
@Transactional
public class NoteService {

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private RevisionService revisionService;

	public String createNote(final CreateNoteRequest createNoteRequest) {
		final var noteEntity = noteRepository.save(toNoteEntity(createNoteRequest));

		// Create revision
		revisionService.createRevision(noteEntity);

		return noteEntity.getId();
	}

	public Note updateNote(final String id, final UpdateNoteRequest updateNoteRequest) {
		final var noteEntity = noteRepository.findById(id).orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ERROR_NOTE_NOT_FOUND, id)));

		toNoteEntity(noteEntity, updateNoteRequest);
		noteRepository.flush();

		revisionService.createRevision(noteEntity);

		return toNote(noteEntity);
	}

	public Note getNoteById(final String id) {
		final var noteEntity = noteRepository.findById(id).orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ERROR_NOTE_NOT_FOUND, id)));

		return toNote(noteEntity);
	}

	public FindNotesResponse getNotes(final FindNotesRequest findNotesRequest) {
		final var matches = noteRepository.findAllByParameters(findNotesRequest, PageRequest.of(findNotesRequest.getPage() - 1,
			findNotesRequest.getLimit(), Sort.by("created").descending()));

		// If page larger than last page is requested, an empty list is returned otherwise the current page
		final List<Note> notes = matches.getTotalPages() < findNotesRequest.getPage() ? emptyList() : toNotes(matches.getContent());

		return FindNotesResponse.create()
			.withMetaData(MetaData.create()
				.withPage(findNotesRequest.getPage())
				.withTotalPages(matches.getTotalPages())
				.withTotalRecords(matches.getTotalElements())
				.withCount(notes.size())
				.withLimit(findNotesRequest.getLimit()))
			.withNotes(notes);
	}

	public void deleteNoteById(final String id) {
		if (!noteRepository.existsById(id)) {
			throw Problem.valueOf(NOT_FOUND, format(ERROR_NOTE_NOT_FOUND, id));
		}

		noteRepository.deleteById(id);
	}
}
