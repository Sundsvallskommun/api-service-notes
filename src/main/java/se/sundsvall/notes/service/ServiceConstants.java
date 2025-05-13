package se.sundsvall.notes.service;

public final class ServiceConstants {

	public static final String KEY_CURRENT_REVISION = "x-current-revision";

	static final String REVISION_NOT_FOUND_FOR_ID_AND_VERSION = "No revision with entityId '%s' and version '%s' was found!";
	static final String PROBLEM_DURING_DIFF = "An error occurred during diff of entityId '%s' looking at version '%s' and version '%s'!";
	static final String ERROR_NOTE_NOT_FOUND = "Note with id '%s' not found";
	public static final String KEY_CURRENT_VERSION = "x-current-version";
	public static final String KEY_PREVIOUS_VERSION = "x-previous-version";
	public static final String KEY_PREVIOUS_REVISION = "x-previous-revision";

	private ServiceConstants() {}
}
