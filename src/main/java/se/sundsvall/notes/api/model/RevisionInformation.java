package se.sundsvall.notes.api.model;

import static java.util.Objects.nonNull;

import java.util.Objects;

public class RevisionInformation {

	private Note note;
	private Revision currentRevision;
	private Revision previousRevision;

	public static RevisionInformation create() {
		return new RevisionInformation();
	}

	public boolean isNewRevisionCreated() {
		return nonNull(currentRevision) && nonNull(note);
	}

	public Note getNote() {
		return note;
	}

	public void setNote(final Note note) {
		this.note = note;
	}

	public RevisionInformation withNote(final Note note) {
		this.note = note;
		return this;
	}

	public Revision getCurrentRevision() {
		return currentRevision;
	}

	public void setCurrentRevision(final Revision currentRevision) {
		this.currentRevision = currentRevision;
	}

	public RevisionInformation withCurrentRevision(final Revision currentRevision) {
		this.currentRevision = currentRevision;
		return this;
	}

	public Revision getPreviousRevision() {
		return previousRevision;
	}

	public void setPreviousRevision(final Revision previousRevision) {
		this.previousRevision = previousRevision;
	}

	public RevisionInformation withPreviousRevision(final Revision previousRevision) {
		this.previousRevision = previousRevision;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(currentRevision, previousRevision, note);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final RevisionInformation other)) {
			return false;
		}
		return Objects.equals(currentRevision, other.currentRevision)
			&& Objects.equals(previousRevision, other.previousRevision)
			&& Objects.equals(note, other.note)
			&& Objects.equals(isNewRevisionCreated(), other.isNewRevisionCreated());
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RevisionInformation [note=").append(note)
			.append(", currentRevision=").append(currentRevision)
			.append(", previousRevision=").append(previousRevision)
			.append(", newRevisionCreated=").append(isNewRevisionCreated())
			.append("]");
		return builder.toString();
	}
}
