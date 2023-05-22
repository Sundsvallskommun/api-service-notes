package se.sundsvall.notes.service;

import generated.se.sundsvall.eventlog.EventType;
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
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.api.model.UpdateNoteRequest;
import se.sundsvall.notes.integration.db.NoteRepository;
import se.sundsvall.notes.integration.db.model.NoteEntity;
import se.sundsvall.notes.integration.eventlog.EventlogClient;
import se.sundsvall.notes.service.mapper.EventMapper;

import java.util.List;

import static generated.se.sundsvall.eventlog.EventType.CREATE;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.notes.service.ServiceConstants.ERROR_NOTE_NOT_FOUND;
import static se.sundsvall.notes.service.mapper.EventMapper.toEvent;
import static se.sundsvall.notes.service.mapper.NoteMapper.toNote;
import static se.sundsvall.notes.service.mapper.NoteMapper.toNoteEntity;
import static se.sundsvall.notes.service.mapper.NoteMapper.toNotes;

@Service
@Transactional
public class NoteService {

	private static final String EVENT_LOG_CREATE_NOTE = "Notering har skapats.";
	private static final String EVENT_LOG_UPDATE_NOTE = "Noteringen har uppdaterats.";
	private static final String EVENT_LOG_DELETE_NOTE = "Notering har raderats.";
	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private RevisionService revisionService;

	@Autowired
	private EventlogClient eventlogClient;

	public String createNote(final CreateNoteRequest createNoteRequest) {
		final var noteEntity = noteRepository.save(toNoteEntity(createNoteRequest));

		// Create revision
		final var currentRevision = revisionService.createNoteRevision(noteEntity);

		// Create log-event
		createEvent(CREATE, EVENT_LOG_CREATE_NOTE, noteEntity, currentRevision, null, createNoteRequest.getCreatedBy());
		return noteEntity.getId();
	}

	public Note updateNote(final String id, final UpdateNoteRequest updateNoteRequest) {
		final var noteEntity = noteRepository.findById(id).orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ERROR_NOTE_NOT_FOUND, id)));

		toNoteEntity(noteEntity, updateNoteRequest);
		noteRepository.flush();

		final var latestRevisionId = revisionService.createRevision(noteEntity);

		final var previousRevision = revisionService.getRevisions(noteEntity.getId()).stream().filter(revision -> !revision.getId().equals(latestRevisionId)).findFirst().orElse(null);

		final var latestRevision = revisionService.getLatestNoteRevision(noteEntity.getId());

		// Create log-event
		createEvent(EventType.UPDATE, EVENT_LOG_UPDATE_NOTE, noteEntity, latestRevision, previousRevision, updateNoteRequest.getModifiedBy());

		return toNote(noteEntity);
	}

	public Note getNoteById(final String id) {
		final var noteEntity = noteRepository.findById(id).orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ERROR_NOTE_NOT_FOUND, id)));

		return toNote(noteEntity);
	}

	public FindNotesResponse getNotes(final FindNotesRequest findNotesRequest) {
		final var matches = noteRepository.findAllByParameters(findNotesRequest, PageRequest.of(findNotesRequest.getPage() - 1,
			findNotesRequest.getLimit(), Sort.by("created").descending()));

		// If page larger than last page is requested, a empty list is returned otherwise the current page
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
		final var noteEntity = noteRepository.findById(id).orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(ERROR_NOTE_NOT_FOUND, id)));

		noteRepository.deleteById(id);

		final var latestRevision = revisionService.getLatestNoteRevision(id);

		// Create log-event
		createEvent(EventType.DELETE, EVENT_LOG_DELETE_NOTE, noteEntity, latestRevision, null, null);
	}

	private void createEvent(EventType eventType, String message, NoteEntity noteEntity, Revision currentRevision, Revision previousRevision, String executedByUserId) {
		final var metadata = EventMapper.toMetadataMap(noteEntity, currentRevision, previousRevision);
		eventlogClient.createEvent(
			noteEntity.getId(),
			toEvent(eventType, message, ofNullable(currentRevision).map(Revision::getId).orElse(null), metadata, executedByUserId));

	}
}
