package com.odde.doughnut.models.questionTypes;

import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.QuizQuestion;
import com.odde.doughnut.entities.ReviewPoint;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.models.quizFacotries.QuizQuestionDirector;
import com.odde.doughnut.models.randomizers.NonRandomizer;
import com.odde.doughnut.testability.MakeMe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.odde.doughnut.entities.QuizQuestion.QuestionType.LINK_TARGET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:repository.xml"})
@Transactional
class QuizQuestionTypeLinkTargetTest {
    @Autowired
    MakeMe makeMe;
    UserModel userModel;
    NonRandomizer randomizer = new NonRandomizer();
    Note top;
    Note target;
    Note source;
    Note anotherSource;
    ReviewPoint reviewPoint;


    @BeforeEach
    void setup() {
        userModel = makeMe.aUser().toModelPlease();
        top = makeMe.aNote().byUser(userModel).please();
        target = makeMe.aNote("target").under(top).please();
        source = makeMe.aNote("source").under(top).linkTo(target).please();
        reviewPoint = makeMe.aReviewPointFor(source.getLinks().get(0)).inMemoryPlease();
        makeMe.refresh(top);
    }

    @Test
    void shouldReturnNullIfCannotFindEnoughOptions() {
        QuizQuestion quizQuestion = buildLinkTargetQuizQuestion();
        assertThat(quizQuestion, is(nullValue()));
    }

    @Nested
    class WhenThereAreMoreThanOneOptions {
        @BeforeEach
        void setup() {
            anotherSource = makeMe.aNote("another note").under(top).please();
            makeMe.refresh(top);
        }


        @Test
        void shouldIncludeRightAnswers() {
            QuizQuestion quizQuestion = buildLinkTargetQuizQuestion();
            assertThat(quizQuestion.getDescription(), equalTo("<mark>source</mark> is a specialization of:"));
            assertThat(quizQuestion.getMainTopic(), equalTo(""));
            List<String> options = toOptionStrings(quizQuestion);
            assertThat(anotherSource.getTitle(), in(options));
            assertThat(target.getTitle(), in(options));
        }
    }

    private QuizQuestion buildLinkTargetQuizQuestion() {
        QuizQuestionDirector builder = new QuizQuestionDirector(LINK_TARGET, randomizer, reviewPoint, makeMe.modelFactoryService);
        return builder.buildQuizQuestion();
    }

    private List<String> toOptionStrings(QuizQuestion quizQuestion) {
        return quizQuestion.getOptions().stream().map(QuizQuestion.Option::getDisplay).collect(Collectors.toUnmodifiableList());
    }
}

