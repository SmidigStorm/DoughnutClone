package com.odde.doughnut.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.odde.doughnut.entities.ReviewPoint;
import com.odde.doughnut.entities.json.ReviewPointViewedByUser;
import com.odde.doughnut.exceptions.NoAccessRightException;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.testability.MakeMe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:repository.xml"})
@Transactional
class RestReviewPointControllerTest {
  @Autowired ModelFactoryService modelFactoryService;
  @Autowired MakeMe makeMe;
  private UserModel userModel;
  RestReviewPointController controller;

  @BeforeEach
  void setup() {
    userModel = makeMe.aUser().toModelPlease();
    controller =
        new RestReviewPointController(modelFactoryService, new TestCurrentUserFetcher(userModel));
  }

  @Nested
  class WhenThereIsAReviewPoint {
    ReviewPoint rp;

    @BeforeEach
    void setup() {
      rp = makeMe.aReviewPointFor(makeMe.aNote().please()).by(userModel).please();
    }

    @Test
    void show() throws NoAccessRightException {
      ReviewPointViewedByUser result = controller.show(rp.getId());
      assertThat(result, notNullValue());
    }

    @Test
    void showNonExist() {
      assertThrows(ResponseStatusException.class, () -> controller.show(rp.getId() + 1000));
    }

    @Test
    void remove() {
      controller.removeFromRepeating(rp);
      assertThat(rp.getRemovedFromReview(), is(true));
    }
  }
}
