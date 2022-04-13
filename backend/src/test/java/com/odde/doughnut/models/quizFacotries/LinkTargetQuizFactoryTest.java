package com.odde.doughnut.models.quizFacotries;

import static com.odde.doughnut.entities.QuizQuestion.QuestionType.LINK_TARGET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.ReviewPoint;
import com.odde.doughnut.entities.json.QuizQuestionViewedByUser;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.testability.MakeMe;
import java.util.List;
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
class LinkTargetQuizFactoryTest {
  @Autowired MakeMe makeMe;
  UserModel userModel;
  Note top;
  Note target;
  Note source;
  Note anotherTarget;
  ReviewPoint reviewPoint;

  @BeforeEach
  void setup() {
    userModel = makeMe.aUser().toModelPlease();
    top = makeMe.aNote().byUser(userModel).please();
    target = makeMe.aNote("target").under(top).please();
    source = makeMe.aNote("source").under(top).linkTo(target).please();
    anotherTarget = makeMe.aNote("another note").under(top).please();
    reviewPoint = makeMe.aReviewPointFor(source.getLinks().get(0)).inMemoryPlease();
    makeMe.refresh(top);
  }

  @Test
  void shouldReturnNullIfCannotFindEnoughOptions() {
    makeMe.aLink().between(source, anotherTarget).please();
    makeMe.refresh(top);

    assertThat(buildLinkTargetQuizQuestion(), is(nullValue()));
  }

  @Nested
  class WhenThereAreMoreThanOneOptions {
    @BeforeEach
    void setup() {
      makeMe.refresh(top);
    }

    @Test
    void shouldIncludeRightAnswers() {
      QuizQuestionViewedByUser quizQuestion = buildLinkTargetQuizQuestion();
      assertThat(
          quizQuestion.getDescription(), equalTo("<mark>source</mark> is a specialization of:"));
      assertThat(quizQuestion.getMainTopic(), equalTo(""));
      List<String> options = toOptionStrings(quizQuestion);
      assertThat(anotherTarget.getTitle(), in(options));
      assertThat(target.getTitle(), in(options));
    }
  }

  private QuizQuestionViewedByUser buildLinkTargetQuizQuestion() {
    return makeMe.buildAQuestion(LINK_TARGET, reviewPoint);
  }

  private List<String> toOptionStrings(QuizQuestionViewedByUser quizQuestion) {
    return quizQuestion.getOptions().stream()
        .map(QuizQuestionViewedByUser.Option::getDisplay)
        .toList();
  }
}
