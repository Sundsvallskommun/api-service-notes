package se.sundsvall.notes.service;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.notes.service.ServiceConstants.ERROR_NOTE_NOT_FOUND;
import static se.sundsvall.notes.service.mapper.NoteMapper.toNote;
import static se.sundsvall.notes.service.mapper.NoteMapper.toNoteEntity;
import static se.sundsvall.notes.service.mapper.NoteMapper.toNotes;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;

import jakarta.transaction.Transactional;
import se.sundsvall.notes.api.model.CreateNoteRequest;
import se.sundsvall.notes.api.model.FindNotesRequest;
import se.sundsvall.notes.api.model.FindNotesResponse;
import se.sundsvall.notes.api.model.MetaData;
import se.sundsvall.notes.api.model.Note;
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.api.model.RevisionInformation;
import se.sundsvall.notes.api.model.UpdateNoteRequest;
import se.sundsvall.notes.integration.db.NoteRepository;

@Service
@Transactional
public class NoteService {

	private final NoteRepository noteRepository;
	private final RevisionService revisionService;

	NoteService(NoteRepository noteRepository, RevisionService revisionService) {
		this.noteRepository = noteRepository;
		this.revisionService = revisionService;
	}

	public RevisionInformation createNote(final CreateNoteRequest createNoteRequest) {
		final var noteEntity = noteRepository.save(toNoteEntity(createNoteRequest));

		// Create revision
		final var currentRevision = revisionService.createRevision(noteEntity);

		return RevisionInformation.create()
			.withNote(toNote(noteEntity))
			.withCurrentRevision(currentRevision);
	}

	public RevisionInformation updateNote(final String id, final UpdateNoteRequest updateNoteRequest) {
		final var noteEntity = noteRepository.findById(id).orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ERROR_NOTE_NOT_FOUND, id)));

		toNoteEntity(noteEntity, updateNoteRequest);
		noteRepository.flush();

		// Create revision
		final var currentRevision = revisionService.createRevision(noteEntity);

		var revisionInformation = RevisionInformation.create()
			.withNote(toNote(noteEntity))
			.withCurrentRevision(currentRevision);

		if (revisionInformation.isNewRevisionCreated()) {
			revisionInformation.setPreviousRevision(getPreviousRevision(noteEntity.getId(), currentRevision));
		}
		return revisionInformation;
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

	public RevisionInformation deleteNoteById(final String id) {
		if (!noteRepository.existsById(id)) {
			throw Problem.valueOf(NOT_FOUND, format(ERROR_NOTE_NOT_FOUND, id));
		}

		noteRepository.deleteById(id);

		final var currentRevision = revisionService.getRevisions(id).stream().findFirst().orElse(null);

		return RevisionInformation.create()
			.withCurrentRevision(currentRevision);
	}

	private Revision getPreviousRevision(final String noteEntityId, final Revision currentRevision) {
		return revisionService.getRevisions(noteEntityId).stream()
			.filter(revision -> !revision.getId().equals(currentRevision.getId())).findFirst().orElse(null);
	}
}
