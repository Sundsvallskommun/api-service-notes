package se.sundsvall.notes.api.model;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.springframework.format.annotation.DateTimeFormat;

@Schema(description = "Revision model")
public class Revision {

	@Schema(description = "Id for the revision")
	private String id;

	@Schema(description = "Id for entity connected to the created revision")
	private String entityId;

	@Schema(description = "Type of entity connected to the created revision")
	private String entityType;

	@Schema(description = "Revision version")
	private Integer version;

	@Schema(description = "Created timestamp")
	@DateTimeFormat(iso = DATE_TIME)
	private OffsetDateTime created;

	public static Revision create() {
		return new Revision();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Revision withId(final String id) {
		this.id = id;
		return this;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(final String entityId) {
		this.entityId = entityId;
	}

	public Revision withEntityId(final String entityId) {
		this.entityId = entityId;
		return this;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(final String entityType) {
		this.entityType = entityType;
	}

	public Revision withEntityType(final String entityType) {
		this.entityType = entityType;
		return this;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(final Integer version) {
		this.version = version;
	}

	public Revision withVersion(final Integer version) {
		this.version = version;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public Revision withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(created, entityId, entityType, id, version);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Revision other)) {
			return false;
		}
		return Objects.equals(created, other.created) && Objects.equals(entityId, other.entityId) && Objects.equals(entityType, other.entityType) && Objects.equals(id, other.id) && Objects.equals(version, other.version);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Revision [id=")
			.append(id).append(", entityId=").append(entityId)
			.append(", entityType=").append(entityType)
			.append(", version=").append(version)
			.append(", created=").append(created)
			.append("]");
		return builder.toString();
	}
}
