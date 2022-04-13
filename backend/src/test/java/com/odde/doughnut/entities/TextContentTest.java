package com.odde.doughnut.entities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.odde.doughnut.testability.MakeMe;
import java.util.Set;
import javax.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:repository.xml"})
@Transactional
public class TextContentTest {

  @Autowired MakeMe makeMe;

  @Nested
  class ValidationTest {
    private Validator validator;
    private final TextContent textContent = makeMe.aNote().inMemoryPlease().getTextContent();

    @BeforeEach
    public void setUp() {
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      validator = factory.getValidator();
    }

    @Test
    public void defaultNoteFromMakeMeIsValidate() {
      assertThat(getViolations(), is(empty()));
    }

    @Test
    public void titleIsNotOptional() {
      textContent.setTitle("");
      assertThat(getViolations(), is(not(empty())));
    }

    @Test
    public void titleCannotBeTooLong() {
      textContent.setTitle(makeMe.aStringOfLength(101));
      assertThat(getViolations(), is(not(empty())));
    }

    private Set<ConstraintViolation<TextContent>> getViolations() {
      return validator.validate(textContent);
    }
  }
}
