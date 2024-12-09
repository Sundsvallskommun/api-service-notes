package se.sundsvall.notes.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.notes.api.model.DifferenceResponse;
import se.sundsvall.notes.api.model.Revision;
import se.sundsvall.notes.service.RevisionService;

@RestController
@Validated
@RequestMapping("/{municipalityId}/notes/{id}/revisions")
@Tag(name = "Revisions", description = "Revision operations")
@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
	Problem.class, ConstraintViolationProblem.class
})))
@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
public class RevisionResource {

	private final RevisionService revisionService;

	RevisionResource(RevisionService revisionService) {
		this.revisionService = revisionService;
	}

	@GetMapping(produces = {
		APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE
	})
	@Operation(summary = "Get revisions by note ID")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	public ResponseEntity<List<Revision>> getRevisionsByNoteId(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "id", description = "Note ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String id) {

		return ok(revisionService.getRevisions(id, municipalityId));
	}

	@GetMapping(path = "/difference", produces = {
		APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE
	})
	@Operation(summary = "Diff revisions by noteId, source and target version")
	@ApiResponse(responseCode = "200", description = "Successful operation", useReturnTypeSchema = true)
	public ResponseEntity<DifferenceResponse> getDifferenceByVersions(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @PathVariable("municipalityId") @ValidMunicipalityId final String municipalityId,
		@Parameter(name = "id", description = "Note ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15") @ValidUuid @PathVariable final String id,
		@Parameter(name = "source", description = "Source version", example = "1", required = true) @Range(min = 0, max = Integer.MAX_VALUE) @RequestParam final Integer source,
		@Parameter(name = "target", description = "Target version", example = "2", required = true) @Range(min = 0, max = Integer.MAX_VALUE) @RequestParam final Integer target) {

		return ok(revisionService.diff(id, municipalityId, source, target));
	}
}
