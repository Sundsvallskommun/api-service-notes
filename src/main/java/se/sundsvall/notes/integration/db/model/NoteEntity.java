package se.sundsvall.notes.integration.db.model;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import org.hibernate.Length;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "note",
	indexes = {
		@Index(name = "note_party_id_index", columnList = "party_id"),
		@Index(name = "note_context_index", columnList = "context"),
		@Index(name = "note_client_id_index", columnList = "client_id"),
		@Index(name = "note_role_index", columnList = "role"),
		@Index(name = "note_municipality_id_index", columnList = "municipality_id")
	})
public class NoteEntity implements Serializable {

	private static final long serialVersionUID = -3451441096651461590L;

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "party_id")
	private String partyId;

	@Column(name = "context")
	private String context;

	@Column(name = "client_id")
	private String clientId;

	@Column(name = "role")
	private String role;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime created;

	@Column(name = "modified_by")
	private String modifiedBy;

	@Column(name = "modified")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime modified;

	@Column(name = "subject")
	private String subject;

	@Column(name = "body", length = Length.LONG32)
	private String body;

	@Column(name = "case_id")
	private String caseId;

	@Column(name = "case_type")
	private String caseType;

	@Column(name = "case_link", length = 512)
	private String caseLink;

	@Column(name = "external_case_id")
	private String externalCaseId;

	@Column(name = "municipality_id", nullable = false)
	private String municipalityId;

	public static NoteEntity create() {
		return new NoteEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public NoteEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(final String partyId) {
		this.partyId = partyId;
	}

	public NoteEntity withPartyId(final String partyId) {
		this.partyId = partyId;
		return this;
	}

	public String getContext() {
		return context;
	}

	public void setContext(final String context) {
		this.context = context;
	}

	public NoteEntity withContext(final String context) {
		this.context = context;
		return this;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(final String clientId) {
		this.clientId = clientId;
	}

	public NoteEntity withClientId(final String clientId) {
		this.clientId = clientId;
		return this;
	}

	public String getRole() {
		return role;
	}

	public void setRole(final String role) {
		this.role = role;
	}

	public NoteEntity withRole(final String role) {
		this.role = role;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public NoteEntity withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public NoteEntity withCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public NoteEntity withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(final String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public NoteEntity withModifiedBy(final String modifiedBy) {
		this.modifiedBy = modifiedBy;
		return this;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public NoteEntity withSubject(final String subject) {
		this.subject = subject;
		return this;
	}

	public String getBody() {
		return body;
	}

	public void setBody(final String body) {
		this.body = body;
	}

	public NoteEntity withBody(final String body) {
		this.body = body;
		return this;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(final String caseId) {
		this.caseId = caseId;
	}

	public NoteEntity withCaseId(final String caseId) {
		this.caseId = caseId;
		return this;
	}

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(final String caseType) {
		this.caseType = caseType;
	}

	public NoteEntity withCaseType(final String caseType) {
		this.caseType = caseType;
		return this;
	}

	public String getCaseLink() {
		return caseLink;
	}

	public void setCaseLink(final String caseLink) {
		this.caseLink = caseLink;
	}

	public NoteEntity withCaseLink(final String caseLink) {
		this.caseLink = caseLink;
		return this;
	}

	public String getExternalCaseId() {
		return externalCaseId;
	}

	public void setExternalCaseId(final String externalCaseId) {
		this.externalCaseId = externalCaseId;
	}

	public NoteEntity withExternalCaseId(final String externalCaseId) {
		this.externalCaseId = externalCaseId;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public NoteEntity withMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	@PrePersist
	void prePersist() {
		created = now(ZoneId.systemDefault()).truncatedTo(MILLIS);
	}

	@PreUpdate
	void preUpdate() {
		modified = now(ZoneId.systemDefault()).truncatedTo(MILLIS);
	}

	@Override
	public int hashCode() {
		return Objects.hash(body, caseId, caseLink, caseType, clientId, context, created, createdBy, externalCaseId, id, modified, modifiedBy, partyId, role, subject, municipalityId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final var other = (NoteEntity) obj;
		return Objects.equals(body, other.body) && Objects.equals(caseId, other.caseId) && Objects.equals(caseLink, other.caseLink) && Objects.equals(caseType, other.caseType) && Objects.equals(clientId, other.clientId) && Objects.equals(context,
			other.context) && Objects.equals(created, other.created) && Objects.equals(createdBy, other.createdBy) && Objects.equals(externalCaseId, other.externalCaseId) && Objects.equals(id, other.id) && Objects.equals(modified, other.modified)
			&& Objects.equals(modifiedBy, other.modifiedBy) && Objects.equals(partyId, other.partyId) && Objects.equals(role, other.role) && Objects.equals(subject, other.subject) && Objects.equals(municipalityId, other.municipalityId);
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("NoteEntity [id=").append(id).append(", partyId=").append(partyId).append(", context=").append(context).append(", clientId=").append(clientId).append(", role=").append(role).append(", createdBy=").append(createdBy).append(
			", created=").append(created).append(", modifiedBy=").append(modifiedBy).append(", modified=").append(modified).append(", subject=").append(subject).append(", body=").append(body).append(", caseId=").append(caseId).append(", caseType=").append(
				caseType).append(", caseLink=").append(caseLink).append(", externalCaseId=").append(externalCaseId).append(", municipalityId=").append(municipalityId).append("]");
		return builder.toString();
	}
}
