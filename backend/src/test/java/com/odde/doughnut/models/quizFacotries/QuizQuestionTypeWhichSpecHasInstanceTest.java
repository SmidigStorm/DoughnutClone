package com.odde.doughnut.models.quizFacotries;

import static com.odde.doughnut.entities.QuizQuestionEntity.QuestionType.WHICH_SPEC_HAS_INSTANCE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.nullValue;

import com.odde.doughnut.controllers.json.QuizQuestion;
import com.odde.doughnut.entities.*;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.testability.MakeMe;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WhichSpecHasInstanceQuizFactoryTest {
  @Autowired MakeMe makeMe;
  UserModel userModel;
  Note top;
  Note target;
  Note source;
  Note anotherSource;
  ReviewPoint reviewPoint;

  @BeforeEach
  void setup() {
    userModel = makeMe.aUser().toModelPlease();
    top = makeMe.aNote("top").creatorAndOwner(userModel).please();
    target = makeMe.aNote("element").under(top).please();
    source = makeMe.aNote("noble gas").under(top).linkTo(target, Link.LinkType.SPECIALIZE).please();
    anotherSource = makeMe.aNote("non-official name").under(top).please();
    reviewPoint =
        makeMe.aReviewPointFor(source.getLinks().get(0).getThing()).by(userModel).inMemoryPlease();
    makeMe.refresh(top);
  }

  @Test
  void shouldBeInvalidWhenNoInsatnceOfLink() {
    assertThat(buildQuestion(), nullValue());
  }

  @Nested
  class WhenTheNoteHasInstance {
    @BeforeEach
    void setup() {
      makeMe.theNote(source).linkTo(anotherSource, Link.LinkType.INSTANCE).please();
    }

    @Test
    void shouldBeInvalidWhenNoInsatnceOfLink() {
      assertThat(buildQuestion(), nullValue());
    }

    @Nested
    class WhenTheNoteHasMoreSpecificationSiblings {
      Note metal;

      @BeforeEach
      void setup() {
        metal = makeMe.aNote("metal").under(top).linkTo(target, Link.LinkType.SPECIALIZE).please();
      }

      @Test
      void shouldBeInvalidWhenNoViceReviewPoint() {
        assertThat(buildQuestion(), nullValue());
      }

      @Nested
      class WhenTheSecondLinkHasReviewPoint {

        @BeforeEach
        void setup() {
          Thing link = source.getLinks().get(1).getThing();

          makeMe.aReviewPointFor(link).by(userModel).please();
          makeMe.refresh(userModel.getEntity());
        }

        @Test
        void shouldIncludeRightAnswers() {
          QuizQuestion quizQuestion = buildQuestion();
          assertThat(
              quizQuestion.getStem(),
              containsString(
                  "<p>Which one is a specialization of <mark>element</mark> <em>and</em> is an instance of <mark>non-official name</mark>:"));
          List<String> strings = toOptionStrings(quizQuestion);
          assertThat("metal", in(strings));
          assertThat(source.getTopicConstructor(), in(strings));
        }

        @Nested
        class PersonAlsoHasTheSameNoteAsInstance {

          @BeforeEach
          void setup() {
            makeMe.theNote(metal).linkTo(anotherSource, Link.LinkType.INSTANCE).please();
          }

          @Test
          void shouldBeInvalid() {
            assertThat(buildQuestion(), nullValue());
          }
        }

        @Nested
        class ChoiceFromInstance {

          @BeforeEach
          void setup() {
            makeMe
                .aNote("something else")
                .under(top)
                .linkTo(anotherSource, Link.LinkType.INSTANCE)
                .please();
            makeMe.refresh(top);
          }

          @Test
          void options() {
            List<String> strings = toOptionStrings(buildQuestion());
            assertThat("something else", in(strings));
          }
        }
      }
    }
  }

  private QuizQuestion buildQuestion() {
    return makeMe.buildAQuestion(WHICH_SPEC_HAS_INSTANCE, this.reviewPoint);
  }

  private List<String> toOptionStrings(QuizQuestion quizQuestion) {
    List<QuizQuestion.Choice> choices = quizQuestion.getChoices();
    return choices.stream().map(QuizQuestion.Choice::getDisplay).toList();
  }
}
