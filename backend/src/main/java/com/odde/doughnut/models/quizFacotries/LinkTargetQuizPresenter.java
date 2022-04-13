package com.odde.doughnut.models.quizFacotries;

import com.odde.doughnut.entities.Link;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.QuizQuestion;

public class LinkTargetQuizPresenter implements QuizQuestionPresenter {
  protected final Link link;
  protected final Note answerNote;

  public LinkTargetQuizPresenter(QuizQuestion quizQuestion) {
    this.link = quizQuestion.getReviewPoint().getLink();
    this.answerNote = link.getTargetNote();
  }

  @Override
  public String mainTopic() {
    return "";
  }

  @Override
  public String instruction() {
    return "<mark>"
        + link.getSourceNote().getTitle()
        + "</mark> is "
        + link.getLinkTypeLabel()
        + ":";
  }
}
