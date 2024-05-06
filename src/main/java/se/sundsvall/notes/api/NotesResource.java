package se.sundsvall.notes.api;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;
import static se.sundsvall.notes.service.ServiceConstants.KEY_CURRENT_REVISION;
import static se.sundsvall.notes.service.ServiceConstants.KEY_CURRENT_VERSION;
import static se.sundsvall.notes.service.ServiceConstants.KEY_PREVIOUS_REVISION;
import static se.sundsvall.notes.service.ServiceConstants.KEY_PREVIOUS_VERSION;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.notes.api.model.CreateNoteRequest;
import se.sundsvall.notes.api.model.FindNotesRequest;
import se.sundsvall.notes.api.model.FindNotesResponse;
import se.sundsvall.notes.api.model.Note;
import se.sundsvall.notes.api.model.RevisionInformation;
import se.sundsvall.notes.api.model.UpdateNoteRequest;
import se.sundsvall.notes.service.NoteService;

@RestController
@Validated
@RequestMapping("/notes")
@Tag(name = "Notes", description = "Note operations")
@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
public class NotesResource {

	private final NoteService noteService;

	NotesResource(NoteService noteService) {
		this.noteService = noteService;
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = {ALL_VALUE, APPLICATION_PROBLEM_JSON_VALUE})
	@Operation(summary = "Create new note")
	@ApiResponse(responseCode = "201", headers = @Header(name = LOCATION, schema = @Schema(type = "string")), description = "Successful operation", useReturnTypeSchema = true)
	public ResponseEntity<Void> createNote(@Valid @NotNull @RequestBody final CreateNoteRequest body) {
		final var revisionInformation = noteService.createNote(body);

		final var headers = new HttpHeaders();
		headers.add(CONTENT_TYPE, ALL_VALUE);

		final var id = ofNullable(revisionInformation.getNote()).map(Note::getId).orElse(null);

		return ResponseEntity
			.created(UriComponentsBuilder.fromPath("/notes/{id}").buildAndExpand(id).toUri())
			.headers(createRevisionHeaders(revisionInformation, headers))
			.build();
	}

	@PatchMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Update note")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Note> updateNote(
		@Parameter(name = "id", description = "Note ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String id,
		@Valid @NotNull @RequestBody final UpdateNoteRequest body) {
		final var revisionInformation = noteService.updateNote(id, body);

		if (revisionInformation.isNewRevisionCreated()) {
			return ResponseEntity
				.ok()
				.headers(createRevisionHeaders(revisionInformation, null))
				.body(revisionInformation.getNote());
		}

		return ResponseEntity.ok().body(revisionInformation.getNote());
	}

	@GetMapping(path = "/{id}", produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Get note by ID")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Note> getNoteById(
		@Parameter(name = "id", description = "Note ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String id) {

		return ok(noteService.getNoteById(id));
	}

	@GetMapping(produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Find all notes filtered by incoming parameters")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	public ResponseEntity<FindNotesResponse> findNotes(
		@Valid final FindNotesRequest searchParams) {
		return ok(noteService.getNotes(searchParams));
	}

	@DeleteMapping(path = "/{id}", produces = { ALL_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Delete note by ID")
	@ApiResponse(responseCode = "204", description = "Successful operation", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Void> deleteNoteById(
		@Parameter(name = "id", description = "Note ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String id) {

		final var revisionInformation = noteService.deleteNoteById(id);

		return noContent()
			.headers(createRevisionHeaders(revisionInformation, null))
			.build();
	}

	private HttpHeaders createRevisionHeaders(RevisionInformation revisionInformation, HttpHeaders additionalHeaders) {
		var httpHeaders = new HttpHeaders();

		ofNullable(revisionInformation.getCurrentRevision()).ifPresent(revision -> {
			httpHeaders.add(KEY_CURRENT_REVISION, revision.getId());
			httpHeaders.add(KEY_CURRENT_VERSION, revision.getVersion().toString());
		});

		ofNullable(revisionInformation.getPreviousRevision()).ifPresent(revision -> {
			httpHeaders.add(KEY_PREVIOUS_REVISION, revision.getId());
			httpHeaders.add(KEY_PREVIOUS_VERSION, revision.getVersion().toString());
		});

		ofNullable(additionalHeaders).ifPresent(httpHeaders::putAll);

		return httpHeaders;
	}
}
