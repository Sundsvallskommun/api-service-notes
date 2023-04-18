package se.sundsvall.notes.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public class Event {

	@Schema(description = "Operation in event", example = "replace", requiredMode = REQUIRED)
	private String op;

	@Schema(description = "Path to attribute", example = "/name/firstName", requiredMode = REQUIRED)
	private String path;

	@Schema(description = "Value of attribute", example = "John", requiredMode = REQUIRED)
	private String value;

	@Schema(description = "Previous value of attribute", example = "John", requiredMode = REQUIRED)
	private String fromValue;

	public static Event create() {
		return new Event();
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public Event withOp(String op) {
		this.op = op;
		return this;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Event withPath(String path) {
		this.path = path;
		return this;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Event withValue(String value) {
		this.value = value;
		return this;
	}

	public String getFromValue() {
		return fromValue;
	}

	public void setFromValue(String fromValue) {
		this.fromValue = fromValue;
	}

	public Event withFromValue(String fromValue) {
		this.fromValue = fromValue;
		return this;
	}
	@Override
	public int hashCode() {
		return Objects.hash(op, path, value, fromValue);
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
		Event other = (Event) obj;
		return Objects.equals(op, other.op) && Objects.equals(path, other.path) && Objects.equals(value, other.value) && Objects.equals(fromValue, other.fromValue);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Event [op=");
		builder.append(op);
		builder.append(", path=");
		builder.append(path);
		builder.append(", value=");
		builder.append(value);
		builder.append(", fromValue=");
		builder.append(fromValue);
		builder.append("]");
		return builder.toString();
	}
}
