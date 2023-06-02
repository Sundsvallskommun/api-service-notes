package se.sundsvall.notes.api.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEqualsExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCodeExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSettersExcluding;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

class RevisionInformationTest {

	@Test
	void testBean() {
		assertThat(RevisionInformation.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSettersExcluding("newRevisionCreated"),
			hasValidBeanHashCodeExcluding("newRevisionCreated"),
			hasValidBeanEqualsExcluding("newRevisionCreated"),
			hasValidBeanToStringExcluding("newRevisionCreated")));
	}

	@Test
	void testBuilderMethods() {

		final var noteId = UUID.randomUUID().toString();
		final var previousRevision = Revision.create().withId(UUID.randomUUID().toString());
		final var currentRevision = Revision.create().withId(UUID.randomUUID().toString());

		final var note = Note.create().withId(noteId);

		final var revisionInformation = RevisionInformation.create()
			.withNote(note)
			.withCurrentRevision(currentRevision)
			.withPreviousRevision(previousRevision);

		assertThat(revisionInformation).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(revisionInformation.getNote()).isNotNull().isEqualTo(note);
		assertThat(revisionInformation.getCurrentRevision()).isNotNull().isEqualTo(currentRevision);
		assertThat(revisionInformation.getPreviousRevision()).isNotNull().isEqualTo(previousRevision);
		assertThat(revisionInformation.isNewRevisionCreated()).isTrue();
	}

	@Test
	void testBuilderMethodsNulls() {

		final var revisionInformation = RevisionInformation.create()
			.withNote(null)
			.withCurrentRevision(null)
			.withPreviousRevision(null);

		assertThat(revisionInformation).isNotNull().hasAllNullFieldsOrProperties();
		assertThat(revisionInformation.isNewRevisionCreated()).isFalse();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(RevisionInformation.create()).hasAllNullFieldsOrProperties();
		assertThat(new RevisionInformation()).hasAllNullFieldsOrProperties();
	}
}
