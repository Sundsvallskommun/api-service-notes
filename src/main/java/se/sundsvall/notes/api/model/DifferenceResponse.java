package se.sundsvall.notes.api.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(description = "DifferenceResponse model")
public class DifferenceResponse {


	@ArraySchema(schema = @Schema(implementation = Event.class, accessMode = READ_ONLY))
	private List<Event> events;

	public static DifferenceResponse create() {
		return new DifferenceResponse();
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public DifferenceResponse withEvents(List<Event> events) {
		this.events = events;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(events);
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
		DifferenceResponse other = (DifferenceResponse) obj;
		return Objects.equals(events, other.events);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DifferenceResponse [events=").append(events).append("]");
		return builder.toString();
	}
}
