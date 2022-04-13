package com.odde.doughnut.models.quizFacotries;

import com.odde.doughnut.entities.Link;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.ReviewPoint;
import com.odde.doughnut.models.NoteViewer;
import com.odde.doughnut.models.UserModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WhichSpecHasInstanceQuizFactory
    implements QuizQuestionFactory, QuestionOptionsFactory {
  private Link cachedInstanceLink = null;
  private List<Note> cachedFillingOptions = null;
  private final ReviewPoint reviewPoint;
  private final Link link;

  public WhichSpecHasInstanceQuizFactory(ReviewPoint reviewPoint) {
    this.reviewPoint = reviewPoint;
    this.link = reviewPoint.getLink();
  }

  @Override
  public List<Note> generateFillingOptions(QuizQuestionServant servant) {
    if (cachedFillingOptions != null) {
      return cachedFillingOptions;
    }
    List<Note> instanceReverse = cachedInstanceLink.getCousinOfSameLinkType(reviewPoint.getUser());
    List<Note> specReverse = link.getCousinOfSameLinkType(reviewPoint.getUser());
    List<Note> backwardPeers =
        Stream.concat(instanceReverse.stream(), specReverse.stream())
            .filter(n -> !(instanceReverse.contains(n) && specReverse.contains(n)))
            .collect(Collectors.toList());
    cachedFillingOptions = servant.randomizer.randomlyChoose(5, backwardPeers);
    return cachedFillingOptions;
  }

  @Override
  public Note generateAnswerNote(QuizQuestionServant servant) {
    cachedInstanceLink = getInstanceLink(servant);
    if (cachedInstanceLink == null) return null;
    return cachedInstanceLink.getSourceNote();
  }

  @Override
  public int minimumOptionCount() {
    return 2;
  }

  @Override
  public int minimumViceReviewPointCount() {
    return 1;
  }

  @Override
  public List<ReviewPoint> getViceReviewPoints(UserModel userModel) {
    if (cachedInstanceLink != null) {
      ReviewPoint reviewPointFor = userModel.getReviewPointFor(cachedInstanceLink);
      if (reviewPointFor != null) {
        return List.of(reviewPointFor);
      }
    }
    return Collections.emptyList();
  }

  private Link getInstanceLink(QuizQuestionServant servant) {
    if (cachedInstanceLink == null) {
      List<Link> candidates = new ArrayList<>();
      populateCandidate(servant, candidates, Link.LinkType.SPECIALIZE);
      populateCandidate(servant, candidates, Link.LinkType.APPLICATION);
      populateCandidate(servant, candidates, Link.LinkType.INSTANCE);
      populateCandidate(servant, candidates, Link.LinkType.TAGGED_BY);
      populateCandidate(servant, candidates, Link.LinkType.ATTRIBUTE);
      populateCandidate(servant, candidates, Link.LinkType.USES);
      populateCandidate(servant, candidates, Link.LinkType.RELATED_TO);
      cachedInstanceLink =
          servant.randomizer.chooseOneRandomly(
              candidates.stream()
                  .filter(
                      l -> {
                        if (l == null) return false;
                        return !link.equals(l);
                      })
                  .collect(Collectors.toList()));
    }
    return cachedInstanceLink;
  }

  private void populateCandidate(
      QuizQuestionServant servant, List<Link> candidates, Link.LinkType specialize) {
    candidates.add(
        servant.randomizer.chooseOneRandomly(
            new NoteViewer(reviewPoint.getUser(), link.getSourceNote())
                .linksOfTypeThroughDirect(List.of(specialize))));
  }

  @Override
  public Link getCategoryLink() {
    return cachedInstanceLink;
  }

  @Override
  public List<Note> knownRightAnswers() {
    return List.of(link.getSourceNote());
  }
}
