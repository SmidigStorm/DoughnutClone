package com.odde.doughnut.models.quizFacotries;

import com.odde.doughnut.entities.Link;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.ReviewPoint;
import com.odde.doughnut.entities.json.LinkViewed;

import java.util.Map;
import java.util.stream.Collectors;

public abstract class ClozeDescriptonQuizFactory implements QuizQuestionFactory {
    protected final ReviewPoint reviewPoint;
    protected final Note answerNote;

    public ClozeDescriptonQuizFactory(ReviewPoint reviewPoint) {
        this.reviewPoint = reviewPoint;
        this.answerNote = getAnswerNote();
    }

    @Override
    public String generateInstruction() {
        return answerNote.getNoteContent().getClozeDescription();
    }

    @Override
    public String generateMainTopic() {
        return "";
    }

    @Override
    public Note generateAnswerNote(QuizQuestionServant servant) {
        return answerNote;
    }

    @Override
    public Map<Link.LinkType, LinkViewed> generateHintLinks() {
        return answerNote.getAllLinks(reviewPoint.getUser()).entrySet().stream()
                .filter(x -> Link.LinkType.openTypes().anyMatch((y)->x.getKey().equals(y)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Note getAnswerNote() {
        return reviewPoint.getNote();
    }

}