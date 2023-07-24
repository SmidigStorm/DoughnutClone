package com.odde.doughnut.factoryServices.quizFacotries.presenters;

import com.odde.doughnut.algorithms.ClozedString;
import com.odde.doughnut.entities.QuizQuestionEntity;

public class DescriptionLinkTargetQuizPresenter extends LinkTargetQuizPresenter {

  public DescriptionLinkTargetQuizPresenter(QuizQuestionEntity quizQuestion) {
    super(quizQuestion);
  }

  @Override
  public String instruction() {
    ClozedString clozeDescription =
        link.getSourceNote().getClozeDescription().hide(answerNote.getNoteTitle());
    return "<p>The following descriptions is "
        + link.getLinkTypeLabel()
        + ":</p>"
        + "<pre style='white-space: pre-wrap;'>"
        + clozeDescription.cloze()
        + "</pre> ";
  }
}