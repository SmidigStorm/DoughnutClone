package com.odde.doughnut.models.quizFacotries;

import com.odde.doughnut.entities.Thingy;
import java.util.ArrayList;
import java.util.List;

public interface QuestionOptionsFactory {
  Thingy generateAnswer() throws QuizQuestionNotPossibleException;

  List<? extends Thingy> generateFillingOptions() throws QuizQuestionNotPossibleException;

  default List<Thingy> getOptionEntities() throws QuizQuestionNotPossibleException {
    Thingy answerNote = generateAnswer();
    if (answerNote == null) return List.of();
    List<Thingy> options = new ArrayList<>(generateFillingOptions());
    options.add(answerNote);
    return options;
  }

  default int minimumOptionCount() {
    return 2;
  }
  ;
}
