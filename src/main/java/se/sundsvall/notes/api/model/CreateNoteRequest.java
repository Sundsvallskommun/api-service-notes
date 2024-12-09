package se.sundsvall.notes.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;

@Schema(description = "CreateNoteRequest model")
public class CreateNoteRequest {

	@Schema(description = "Context for note", example = "SUPPORT", requiredMode = REQUIRED)
	@NotBlank
	@Size(min = 1, max = 255)
	private String context;

	@Schema(description = "Role of note creator", example = "FIRST_LINE_SUPPORT", requiredMode = REQUIRED)
	@NotBlank
	@Size(min = 1, max = 255)
	private String role;

	@Schema(description = "Id of the client who is the owner of the note", example = "SUPPORT_MGMT", requiredMode = REQUIRED)
	@NotBlank
	@Size(min = 1, max = 255)
	private String clientId;

	@Schema(description = "Party id (e.g. a personId or an organizationId)", example = "81471222-5798-11e9-ae24-57fa13b361e1")
	@ValidUuid(nullable = true)
	private String partyId;

	@Schema(description = "The note subject", example = "This is a subject", maximum = "255", requiredMode = REQUIRED)
	@NotBlank
	@Size(min = 1, max = 255)
	private String subject;

	@Schema(description = "The note body", example = "This is a note", maximum = "2048", requiredMode = REQUIRED)
	@NotBlank
	@Size(min = 1, max = 2048)
	private String body;

	@Schema(description = "Created by", example = "John Doe", requiredMode = REQUIRED)
	@NotBlank
	private String createdBy;

	@Schema(description = "Id for the case", example = "12345")
	@Size(min = 1, max = 255)
	private String caseId;

	@Schema(description = "Type of the case", example = "Byggärende")
	@Size(min = 1, max = 255)
	private String caseType;

	@Schema(description = "Link to the case", example = "http://caselink.com/12345")
	@Size(min = 1, max = 512)
	private String caseLink;

	@Schema(description = "External id for the case", example = "2229")
	@Size(min = 1, max = 255)
	private String externalCaseId;

	public static CreateNoteRequest create() {
		return new CreateNoteRequest();
	}

	public String getContext() {
		return context;
	}

	public void setContext(final String context) {
		this.context = context;
	}

	public CreateNoteRequest withContext(final String context) {
		this.context = context;
		return this;
	}

	public String getRole() {
		return role;
	}

	public void setRole(final String role) {
		this.role = role;
	}

	public CreateNoteRequest withRole(final String role) {
		this.role = role;
		return this;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(final String clientId) {
		this.clientId = clientId;
	}

	public CreateNoteRequest withClientId(final String clientId) {
		this.clientId = clientId;
		return this;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(final String partyId) {
		this.partyId = partyId;
	}

	public CreateNoteRequest withPartyId(final String partyId) {
		this.partyId = partyId;
		return this;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public CreateNoteRequest withSubject(final String subject) {
		this.subject = subject;
		return this;
	}

	public String getBody() {
		return body;
	}

	public void setBody(final String body) {
		this.body = body;
	}

	public CreateNoteRequest withBody(final String body) {
		this.body = body;
		return this;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public CreateNoteRequest withCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(final String caseId) {
		this.caseId = caseId;
	}

	public CreateNoteRequest withCaseId(final String caseId) {
		this.caseId = caseId;
		return this;
	}

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(final String caseType) {
		this.caseType = caseType;
	}

	public CreateNoteRequest withCaseType(final String caseType) {
		this.caseType = caseType;
		return this;
	}

	public String getCaseLink() {
		return caseLink;
	}

	public void setCaseLink(final String caseLink) {
		this.caseLink = caseLink;
	}

	public CreateNoteRequest withCaseLink(final String caseLink) {
		this.caseLink = caseLink;
		return this;
	}

	public String getExternalCaseId() {
		return externalCaseId;
	}

	public void setExternalCaseId(final String externalCaseId) {
		this.externalCaseId = externalCaseId;
	}

	public CreateNoteRequest withExternalCaseId(final String externalCaseId) {
		this.externalCaseId = externalCaseId;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(body, caseId, caseLink, caseType, clientId, context, createdBy, externalCaseId, partyId, role, subject);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final var other = (CreateNoteRequest) obj;
		return Objects.equals(body, other.body) && Objects.equals(caseId, other.caseId) && Objects.equals(caseLink, other.caseLink) && Objects.equals(caseType, other.caseType) && Objects.equals(clientId, other.clientId) && Objects.equals(context,
			other.context) && Objects.equals(createdBy, other.createdBy) && Objects.equals(externalCaseId, other.externalCaseId) && Objects.equals(partyId, other.partyId) && Objects.equals(role, other.role) && Objects.equals(subject, other.subject);
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append("CreateNoteRequest [context=").append(context)
			.append(", role=").append(role)
			.append(", clientId=").append(clientId)
			.append(", partyId=").append(partyId)
			.append(", subject=").append(subject)
			.append(", body=").append(body)
			.append(", createdBy=").append(createdBy)
			.append(", caseId=").append(caseId)
			.append(", caseType=").append(caseType)
			.append(", caseLink=").append(caseLink)
			.append(", externalCaseId=").append(externalCaseId)
			.append("]").toString();
	}
}
