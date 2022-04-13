package com.odde.doughnut.models.quizFacotries;

import com.odde.doughnut.entities.Link;
import com.odde.doughnut.entities.QuizQuestion;

public class WhichSpecHasInstanceQuizPresenter implements QuizQuestionPresenter {
  private Link instanceLink;
  private final Link link;

  public WhichSpecHasInstanceQuizPresenter(QuizQuestion quizQuestion) {
    this.link = quizQuestion.getReviewPoint().getLink();
    this.instanceLink = quizQuestion.getCategoryLink();
  }

  @Override
  public String mainTopic() {
    return null;
  }

  @Override
  public String instruction() {
    return "<p>Which one is "
        + link.getLinkTypeLabel()
        + " <mark>"
        + link.getTargetNote().getTitle()
        + "</mark> <em>and</em> is "
        + instanceLink.getLinkTypeLabel()
        + " <mark>"
        + instanceLink.getTargetNote().getTitle()
        + "</mark>:";
  }
}
