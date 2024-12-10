package se.sundsvall.notes.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;

@Schema(description = "DifferenceResponse model")
public class DifferenceResponse {

	@ArraySchema(schema = @Schema(implementation = Operation.class, accessMode = READ_ONLY))
	private List<Operation> operations;

	public static DifferenceResponse create() {
		return new DifferenceResponse();
	}

	public List<Operation> getOperations() {
		return operations;
	}

	public void setOperations(final List<Operation> operations) {
		this.operations = operations;
	}

	public DifferenceResponse withOperations(final List<Operation> operations) {
		this.operations = operations;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(operations);
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
		final DifferenceResponse other = (DifferenceResponse) obj;
		return Objects.equals(operations, other.operations);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DifferenceResponse [operations=").append(operations).append("]");
		return builder.toString();
	}
}
