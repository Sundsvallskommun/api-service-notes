package se.sundsvall.notes.service;

public class ServiceConstants {

	private ServiceConstants() {}

	static final String REVISION_NOT_FOUND_FOR_ID_AND_VERSION = "No revision with entityId '%s' and version '%s' was found!";
	static final String PROBLEM_DURING_DIFF = "An error occured during diff of entityId '%s' looking at version '%s' and version '%s'!";
	static final String ERROR_NOTE_NOT_FOUND = "Note with id '%s' not found";

	static final String EVENT_LOG_CREATE_NOTE = "Notering har skapats.";
	static final String EVENT_LOG_UPDATE_NOTE = "Noteringen har uppdaterats.";
	static final String EVENT_LOG_DELETE_NOTE = "Notering har raderats.";
}
