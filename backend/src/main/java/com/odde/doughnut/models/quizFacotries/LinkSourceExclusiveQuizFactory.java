package com.odde.doughnut.models.quizFacotries;

import com.odde.doughnut.entities.Link;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.ReviewPoint;
import java.util.ArrayList;
import java.util.List;

public class LinkSourceExclusiveQuizFactory implements QuizQuestionFactory, QuestionOptionsFactory {
  private final Link link;
  private final ReviewPoint reviewPoint;
  private List<Note> cachedFillingOptions = null;
  private Note answerNote = null;

  public LinkSourceExclusiveQuizFactory(ReviewPoint reviewPoint) {
    this.reviewPoint = reviewPoint;
    this.link = reviewPoint.getLink();
  }

  @Override
  public List<Note> generateFillingOptions(QuizQuestionServant servant) {
    if (cachedFillingOptions == null) {
      Note sourceNote = link.getSourceNote();
      List<Note> backwardPeers = link.getCousinOfSameLinkType(reviewPoint.getUser());
      cachedFillingOptions = servant.randomlyChooseAndEnsure(backwardPeers, sourceNote);
    }
    return cachedFillingOptions;
  }

  @Override
  public Note generateAnswerNote(QuizQuestionServant servant) {
    if (answerNote == null) {
      Note note = link.getSourceNote();
      List<Note> siblings = new ArrayList<>(note.getSiblings());
      siblings.removeAll(link.getCousinOfSameLinkType(reviewPoint.getUser()));
      siblings.remove(link.getTargetNote());
      siblings.remove(link.getSourceNote());
      answerNote = servant.randomizer.chooseOneRandomly(siblings);
    }
    return answerNote;
  }

  @Override
  public int minimumOptionCount() {
    return 2;
  }

  @Override
  public List<Note> allWrongAnswers() {
    return List.of(link.getSourceNote());
  }
}
