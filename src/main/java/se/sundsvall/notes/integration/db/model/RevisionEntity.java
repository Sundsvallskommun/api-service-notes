package se.sundsvall.notes.integration.db.model;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.hibernate.Length.LONG32;
import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "revision",
	indexes = {
		@Index(name = "revision_entity_id_index", columnList = "entity_id"),
		@Index(name = "revision_entity_type_index", columnList = "entity_type"),
		@Index(name = "revision_municipality_id_index", columnList = "municipality_id")
	})
public class RevisionEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "entity_id")
	private String entityId;

	@Column(name = "entity_type")
	private String entityType;

	@Column(name = "version")
	private Integer version;

	@Column(name = "serialized_snapshot", length = LONG32)
	private String serializedSnapshot;

	@Column(name = "municipality_id", nullable = false)
	private String municipalityId;

	@Column(name = "created")
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime created;

	public static RevisionEntity create() {
		return new RevisionEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public RevisionEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(final String entityId) {
		this.entityId = entityId;
	}

	public RevisionEntity withEntityId(final String entityId) {
		this.entityId = entityId;
		return this;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(final String entityType) {
		this.entityType = entityType;
	}

	public RevisionEntity withEntityType(final String entityType) {
		this.entityType = entityType;
		return this;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(final Integer version) {
		this.version = version;
	}

	public RevisionEntity withVersion(final Integer version) {
		this.version = version;
		return this;
	}

	public String getSerializedSnapshot() {
		return serializedSnapshot;
	}

	public void setSerializedSnapshot(final String serializedSnapshot) {
		this.serializedSnapshot = serializedSnapshot;
	}

	public RevisionEntity withSerializedSnapshot(final String serializedSnapshot) {
		this.serializedSnapshot = serializedSnapshot;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public RevisionEntity withMunicipalityId(final String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public RevisionEntity withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	@PrePersist
	void prePersist() {
		created = now(systemDefault()).truncatedTo(MILLIS);
	}

	@Override
	public int hashCode() {
		return Objects.hash(created, entityId, entityType, id, serializedSnapshot, version, municipalityId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final RevisionEntity other)) {
			return false;
		}
		return Objects.equals(created, other.created) && Objects.equals(entityId, other.entityId) && Objects.equals(entityType, other.entityType) && Objects.equals(id, other.id) && Objects.equals(serializedSnapshot, other.serializedSnapshot) && Objects
			.equals(version, other.version) && Objects.equals(municipalityId, other.municipalityId);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RevisionEntity [id=").append(id).append(", entityId=").append(entityId).append(", entityType=").append(entityType).append(", version=").append(version).append(", serializedSnapshot=").append(serializedSnapshot)
			.append(", municipalityId=").append(municipalityId).append(", created=").append(created).append("]");
		return builder.toString();
	}
}
