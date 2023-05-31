package se.sundsvall.notes.service;

public class ServiceConstants {

	private ServiceConstants() {}

	static final String REVISION_NOT_FOUND_FOR_ID_AND_VERSION = "No revision with entityId '%s' and version '%s' was found!";
	static final String PROBLEM_DURING_DIFF = "An error occurred during diff of entityId '%s' looking at version '%s' and version '%s'!";
	static final String ERROR_NOTE_NOT_FOUND = "Note with id '%s' not found";
	public static final String KEY_NOTE = "note";
	public static final String KEY_REVISION_ID = "revisionId";
}
