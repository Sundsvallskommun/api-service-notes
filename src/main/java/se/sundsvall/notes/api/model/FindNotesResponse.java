package se.sundsvall.notes.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "FindNotesResponse model")
public class FindNotesResponse {

	@JsonProperty("_meta")
	@Schema(implementation = MetaData.class, accessMode = READ_ONLY)
	private MetaData metaData;

	@ArraySchema(schema = @Schema(implementation = Note.class, accessMode = READ_ONLY))
	private List<Note> notes;

	public static FindNotesResponse create() {
		return new FindNotesResponse();
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public FindNotesResponse withMetaData(MetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public FindNotesResponse withNotes(List<Note> notes) {
		this.notes = notes;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(notes, metaData);
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
		FindNotesResponse other = (FindNotesResponse) obj;
		return Objects.equals(notes, other.notes) && Objects.equals(metaData, other.metaData);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FindNotesResponse [metaData=").append(metaData).append(", notes=").append(notes).append("]");
		return builder.toString();
	}
}
