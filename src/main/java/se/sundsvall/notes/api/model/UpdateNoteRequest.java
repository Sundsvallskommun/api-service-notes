package se.sundsvall.notes.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;

@Schema(description = "UpdateNoteRequest model")
public class UpdateNoteRequest {

	@Schema(description = "The note subject", examples = "This is a subject", exclusiveMaximumValue = 256)
	@Size(min = 1, max = 255)
	private String subject;

	@Schema(description = "The note body", examples = "This is a note", exclusiveMaximumValue = 2049)
	@Size(min = 1, max = 2048)
	private String body;

	@Schema(description = "Modified by", examples = "John Doe", requiredMode = REQUIRED)
	@NotBlank
	private String modifiedBy;

	@Schema(description = "Id for the case", examples = "12345")
	@Size(min = 1, max = 255)
	private String caseId;

	@Schema(description = "Type of the case", examples = "Byggärende")
	@Size(min = 1, max = 255)
	private String caseType;

	@Schema(description = "Link to the case", examples = "http://caselink.com/12345")
	@Size(min = 1, max = 512)
	private String caseLink;

	@Schema(description = "External id for the case", examples = "2229")
	@Size(min = 1, max = 255)
	private String externalCaseId;

	public static UpdateNoteRequest create() {
		return new UpdateNoteRequest();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public UpdateNoteRequest withSubject(final String subject) {
		this.subject = subject;
		return this;
	}

	public String getBody() {
		return body;
	}

	public void setBody(final String body) {
		this.body = body;
	}

	public UpdateNoteRequest withBody(final String body) {
		this.body = body;
		return this;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(final String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public UpdateNoteRequest withModifiedBy(final String modifiedBy) {
		this.modifiedBy = modifiedBy;
		return this;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(final String caseId) {
		this.caseId = caseId;
	}

	public UpdateNoteRequest withCaseId(final String caseId) {
		this.caseId = caseId;
		return this;
	}

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(final String caseType) {
		this.caseType = caseType;
	}

	public UpdateNoteRequest withCaseType(final String caseType) {
		this.caseType = caseType;
		return this;
	}

	public String getCaseLink() {
		return caseLink;
	}

	public void setCaseLink(final String caseLink) {
		this.caseLink = caseLink;
	}

	public UpdateNoteRequest withCaseLink(final String caseLink) {
		this.caseLink = caseLink;
		return this;
	}

	public String getExternalCaseId() {
		return externalCaseId;
	}

	public void setExternalCaseId(final String externalCaseId) {
		this.externalCaseId = externalCaseId;
	}

	public UpdateNoteRequest withExternalCaseId(final String externalCaseId) {
		this.externalCaseId = externalCaseId;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(body, caseId, caseLink, caseType, externalCaseId, modifiedBy, subject);
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
		final var other = (UpdateNoteRequest) obj;
		return Objects.equals(body, other.body) && Objects.equals(caseId, other.caseId) && Objects.equals(caseLink, other.caseLink) && Objects.equals(caseType, other.caseType) && Objects.equals(externalCaseId, other.externalCaseId) && Objects.equals(
			modifiedBy, other.modifiedBy) && Objects.equals(subject, other.subject);
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("UpdateNoteRequest [subject=").append(subject).append(", body=").append(body).append(", modifiedBy=").append(modifiedBy).append(", caseId=").append(caseId).append(", caseType=").append(caseType).append(", caseLink=").append(
			caseLink).append(", externalCaseId=").append(externalCaseId).append("]");
		return builder.toString();
	}
}
