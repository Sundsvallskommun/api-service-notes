package se.sundsvall.notes.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class FindNotesResponseTest {

	@Test
	void testBean() {
		assertThat(FindNotesResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final var metaData = MetaData.create();
		final var notes = List.of(Note.create());

		final var bean = FindNotesResponse.create()
			.withMetaData(metaData)
			.withNotes(notes);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getMetaData()).isEqualTo(metaData);
		assertThat(bean.getNotes()).isEqualTo(notes);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(FindNotesResponse.create()).hasAllNullFieldsOrProperties();
	}
}
