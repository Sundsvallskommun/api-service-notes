package se.sundsvall.notes.api.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.time.OffsetDateTime;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(NON_NULL)
@Schema(description = "Note model")
public class Note {

	@Schema(description = "Note ID", example = "b82bd8ac-1507-4d9a-958d-369261eecc15")
	private String id;

	@Schema(description = "Context for note", example = "SUPPORT")
	private String context;

	@Schema(description = "Role of note creator", example = "FIRST_LINE_SUPPORT")
	private String role;

	@Schema(description = "Id of the client who is the owner of the note", example = "SUPPORT_MGMT")
	private String clientId;

	@Schema(description = "Party ID (e.g. a personId or an organizationId)", example = "81471222-5798-11e9-ae24-57fa13b361e1")
	private String partyId;

	@Schema(description = "The note subject", example = "This is a subject")
	private String subject;

	@Schema(description = "The note body", example = "This is a note")
	private String body;

	@Schema(description = "Id for the case", example = "12345")
	private String caseId;

	@Schema(description = "Type of the case", example = "Byggärende")
	private String caseType;

	@Schema(description = "Link to the case", example = "http://test.sundsvall.se/case1337")
	private String caseLink;

	@Schema(description = "External id for the case", example = "2229")
	private String externalCaseId;

	@Schema(description = "Created by", example = "John Doe")
	private String createdBy;

	@Schema(description = "Modified by", example = "John Doe")
	private String modifiedBy;

	@Schema(description = "Created timestamp")
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private OffsetDateTime created;

	@Schema(description = "Modified timestamp")
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private OffsetDateTime modified;

	public static Note create() {
		return new Note();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Note withId(String id) {
		this.id = id;
		return this;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public Note withContext(String context) {
		this.context = context;
		return this;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Note withRole(String role) {
		this.role = role;
		return this;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Note withClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public Note withPartyId(String partyId) {
		this.partyId = partyId;
		return this;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Note withSubject(String subject) {
		this.subject = subject;
		return this;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Note withBody(String body) {
		this.body = body;
		return this;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public Note withCaseId(String caseId) {
		this.caseId = caseId;
		return this;
	}

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	public Note withCaseType(String caseType) {
		this.caseType = caseType;
		return this;
	}

	public String getCaseLink() {
		return caseLink;
	}

	public void setCaseLink(String caseLink) {
		this.caseLink = caseLink;
	}

	public Note withCaseLink(String caseLink) {
		this.caseLink = caseLink;
		return this;
	}

	public String getExternalCaseId() {
		return externalCaseId;
	}

	public void setExternalCaseId(String externalCaseId) {
		this.externalCaseId = externalCaseId;
	}

	public Note withExternalCaseId(String externalCaseId) {
		this.externalCaseId = externalCaseId;
		return this;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Note withCreatedBy(String createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Note withModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public Note withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(OffsetDateTime modified) {
		this.modified = modified;
	}

	public Note withModified(OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(body, caseId, caseLink, caseType, clientId, context, created, createdBy, externalCaseId, id, modified, modifiedBy, partyId, role, subject);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Note other = (Note) obj;
		return Objects.equals(body, other.body) && Objects.equals(caseId, other.caseId) && Objects.equals(caseLink, other.caseLink) && Objects.equals(caseType, other.caseType) && Objects.equals(clientId, other.clientId) && Objects.equals(context,
			other.context) && Objects.equals(created, other.created) && Objects.equals(createdBy, other.createdBy) && Objects.equals(externalCaseId, other.externalCaseId) && Objects.equals(id, other.id) && Objects.equals(modified, other.modified)
			&& Objects.equals(modifiedBy, other.modifiedBy) && Objects.equals(partyId, other.partyId) && Objects.equals(role, other.role) && Objects.equals(subject, other.subject);
	}

	@Override
	public String toString() {
		return new StringBuilder().append("Note [id=").append(id)
			.append(", context=").append(context)
			.append(", role=").append(role)
			.append(", clientId=").append(clientId)
			.append(", partyId=").append(partyId)
			.append(", subject=").append(subject)
			.append(", body=").append(body)
			.append(", caseId=").append(caseId)
			.append(", caseType=").append(caseType)
			.append(", caseLink=").append(caseLink)
			.append(", externalCaseId=").append(externalCaseId)
			.append(", createdBy=").append(createdBy)
			.append(", modifiedBy=").append(modifiedBy)
			.append(", created=").append(created)
			.append(", modified=").append(modified)
			.append("]").toString();
	}
}
