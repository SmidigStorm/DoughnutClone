
package com.odde.doughnut.controllers;

import com.odde.doughnut.controllers.currentUser.CurrentUserFetcher;
import com.odde.doughnut.entities.*;
import com.odde.doughnut.entities.json.*;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import com.odde.doughnut.models.ReviewPointModel;
import com.odde.doughnut.models.Reviewing;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.testability.TestabilitySettings;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/reviews")
class RestReviewsController {
    private final ModelFactoryService modelFactoryService;
    private final CurrentUserFetcher currentUserFetcher;
    @Resource(name = "testabilitySettings")
    private final TestabilitySettings testabilitySettings;


    public RestReviewsController(ModelFactoryService modelFactoryService, CurrentUserFetcher currentUserFetcher, TestabilitySettings testabilitySettings) {
        this.modelFactoryService = modelFactoryService;
        this.currentUserFetcher = currentUserFetcher;
        this.testabilitySettings = testabilitySettings;
    }

    @GetMapping("/overview")
    public ReviewStatus overview() {
        UserModel user = currentUserFetcher.getUser();
        user.getAuthorization().assertLoggedIn();
        return user.createReviewing(testabilitySettings.getCurrentUTCTimestamp()).getReviewStatus();
    }

    @GetMapping("/initial")
    public ReviewPointViewedByUser initialReview() {
        UserModel user = currentUserFetcher.getUser();
        user.getAuthorization().assertLoggedIn();
        Reviewing reviewing = user.createReviewing(testabilitySettings.getCurrentUTCTimestamp());
        ReviewPoint reviewPoint = reviewing.getOneInitialReviewPoint();
        ReviewPointViewedByUser from = ReviewPointViewedByUser.from(reviewPoint, user);
        from.setRemainingInitialReviewCountForToday(reviewing.toInitialReviewCount());
        return from;
    }

    @PostMapping(path = "")
    @Transactional
    public ReviewPointViewedByUser create(@RequestBody InitialInfo initialInfo) {
        UserModel userModel = currentUserFetcher.getUser();
        userModel.getAuthorization().assertLoggedIn();
        if (initialInfo.reviewPoint.getNoteId() != null) {
            initialInfo.reviewPoint.setNote(modelFactoryService.noteRepository.findById(initialInfo.reviewPoint.getNoteId()).orElse(null));
        }
        if (initialInfo.reviewPoint.getLinkId() != null) {
            initialInfo.reviewPoint.setLink(modelFactoryService.linkRepository.findById(initialInfo.reviewPoint.getLinkId()).orElse(null));
        }
        ReviewPointModel reviewPointModel = modelFactoryService.toReviewPointModel(initialInfo.reviewPoint);
        reviewPointModel.initialReview(userModel, initialInfo.reviewSetting, testabilitySettings.getCurrentUTCTimestamp());
        return initialReview();
    }

    @GetMapping("/repeat")
    @Transactional
    public RepetitionForUser repeatReview() {
        UserModel user = currentUserFetcher.getUser();
        user.getAuthorization().assertLoggedIn();
        Reviewing reviewing = user.createReviewing(testabilitySettings.getCurrentUTCTimestamp());
        return reviewing.getOneRepetitionForUser(user, testabilitySettings.getRandomizer());
    }

    @PostMapping("/answer")
    @Transactional
    public AnswerResult answerQuiz(@Valid @RequestBody Answer answer) {
        UserModel user = currentUserFetcher.getUser();
        user.getAuthorization().assertLoggedIn();
        AnswerResult answerResult = new AnswerResult();
        answerResult.setReviewPoint(answer.getQuestion().getReviewPoint());
        answerResult.setQuestionType(answer.getQuestion().getQuestionType());
        answerResult.setAnswer(answer.getAnswer());
        if (answer.getAnswerNoteId() != null) {
            answerResult.setAnswerNote(modelFactoryService.noteRepository.findById(answer.getAnswerNoteId()).orElse(null));
        }
        Timestamp currentUTCTimestamp = testabilitySettings.getCurrentUTCTimestamp();
        answer.getQuestion().getViceReviewPointIdList().forEach(rPid ->
                modelFactoryService.reviewPointRepository
                        .findById(rPid).ifPresent(vice -> modelFactoryService.toReviewPointModel(vice).updateReviewPoint(answerResult.isCorrect(), currentUTCTimestamp))
        );
        modelFactoryService.toReviewPointModel(answer.getQuestion().getReviewPoint()).updateReviewPoint(answerResult.isCorrect(), currentUTCTimestamp);
        return answerResult;
    }

    @PostMapping(path = "/{reviewPoint}/self-evaluate")
    public RepetitionForUser selfEvaluate(ReviewPoint reviewPoint, @RequestBody SelfEvaluation selfEvaluation) {
        if (reviewPoint == null || reviewPoint.getId() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The review point does not exist.");
        }
        UserModel user = currentUserFetcher.getUser();
        user.getAuthorization().assertLoggedIn();
        evaluate(reviewPoint, selfEvaluation);
        return repeatReview();
    }

    private void evaluate(ReviewPoint reviewPoint, SelfEvaluation selfEvaluation) {
        ReviewPointModel reviewPointModel = modelFactoryService.toReviewPointModel(reviewPoint);
        if (selfEvaluation.increaseRepeatCount != null && selfEvaluation.increaseRepeatCount) {
            reviewPointModel.increaseRepetitionCountAndSave();
        }
        if ("again".equals(selfEvaluation.selfEvaluation)) {
            return;
        }
        if ("satisfying".equals(selfEvaluation.selfEvaluation)) {
            reviewPointModel.repeated(testabilitySettings.getCurrentUTCTimestamp());
            return;
        }
        if ("sad".equals(selfEvaluation.selfEvaluation)) {
            reviewPointModel.repeatedSad(testabilitySettings.getCurrentUTCTimestamp());
            return;
        }
        if ("happy".equals(selfEvaluation.selfEvaluation)) {
            reviewPointModel.repeatedHappy(testabilitySettings.getCurrentUTCTimestamp());
            return;
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

}
