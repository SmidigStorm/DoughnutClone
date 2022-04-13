package com.odde.doughnut.models.quizFacotries;

import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.ReviewPoint;
import java.util.List;

public record JustReviewQuizFactory(ReviewPoint reviewPoint) implements QuizQuestionFactory {

  @Override
  public boolean isValidQuestion() {
    return true;
  }

  @Override
  public List<Note> allWrongAnswers() {
    return List.of();
  }
}
