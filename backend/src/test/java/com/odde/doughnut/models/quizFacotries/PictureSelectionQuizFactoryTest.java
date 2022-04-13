package com.odde.doughnut.models.quizFacotries;

import static com.odde.doughnut.entities.QuizQuestion.QuestionType.PICTURE_SELECTION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.odde.doughnut.entities.AnswerViewedByUser;
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
class PictureSelectionQuizFactoryTest {
  @Autowired MakeMe makeMe;
  UserModel userModel;
  Note top;
  Note father;
  Note source;
  Note brother;
  Note uncle;
  ReviewPoint reviewPoint;

  @BeforeEach
  void setup() {
    userModel = makeMe.aUser().toModelPlease();
    top = makeMe.aNote().byUser(userModel).please();
    father = makeMe.aNote("second level").under(top).please();
    uncle = makeMe.aNote("uncle").under(top).please();
    source = makeMe.aNote("source").under(father).please();
    brother = makeMe.aNote("another note").under(father).please();
    reviewPoint = makeMe.aReviewPointFor(source).inMemoryPlease();
    makeMe.refresh(top);
    makeMe.refresh(father);
  }

  @Test
  void shouldReturnNullIfCannotFindPicture() {
    assertThat(buildLinkTargetQuizQuestion(), is(nullValue()));
  }

  @Nested
  class WhenThereIsPicture {
    @BeforeEach
    void setup() {
      makeMe.theNote(source).pictureUrl("http://img/img.jpg").please();
    }

    @Test
    void shouldReturnNullIfCannotFindPicture() {
      assertThat(buildLinkTargetQuizQuestion(), is(nullValue()));
    }

    @Nested
    class WhenThereIsAnotherPictureNote {
      @BeforeEach
      void setup() {
        makeMe.theNote(brother).pictureUrl("http://img/img2.jpg").please();
      }

      @Test
      void shouldIncludeRightAnswers() {
        QuizQuestionViewedByUser quizQuestion = buildLinkTargetQuizQuestion();
        assertThat(quizQuestion.getDescription(), equalTo(""));
        assertThat(quizQuestion.getMainTopic(), equalTo("source"));
        List<String> options = toOptionStrings(quizQuestion);
        assertThat(source.getTitle(), in(options));
      }
    }

    @Nested
    class WhenThereIsAnotherPictureInUncleNote {
      @BeforeEach
      void setup() {
        makeMe.theNote(uncle).pictureUrl("http://img/img2.jpg").please();
      }

      @Test
      void shouldIncludeUncle() {
        QuizQuestionViewedByUser quizQuestion = buildLinkTargetQuizQuestion();
        List<String> options = toOptionStrings(quizQuestion);
        assertThat(uncle.getTitle(), in(options));
      }
    }

    @Nested
    class Answer {
      @Test
      void correct() {
        AnswerViewedByUser answerResult =
            makeMe
                .anAnswerFor(reviewPoint)
                .type(PICTURE_SELECTION)
                .answer(source.getTitle())
                .inMemoryPlease();
        assertTrue(answerResult.correct);
      }
    }
  }

  private QuizQuestionViewedByUser buildLinkTargetQuizQuestion() {
    return makeMe.buildAQuestion(PICTURE_SELECTION, reviewPoint);
  }

  private List<String> toOptionStrings(QuizQuestionViewedByUser quizQuestion) {
    return quizQuestion.getOptions().stream()
        .map(QuizQuestionViewedByUser.Option::getDisplay)
        .toList();
  }
}
