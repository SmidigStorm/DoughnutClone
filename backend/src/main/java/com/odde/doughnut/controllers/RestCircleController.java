package com.odde.doughnut.controllers;

import com.odde.doughnut.controllers.currentUser.CurrentUserFetcher;
import com.odde.doughnut.entities.Circle;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.TextContent;
import com.odde.doughnut.entities.json.CircleForUserView;
import com.odde.doughnut.entities.json.CircleJoiningByInvitation;
import com.odde.doughnut.entities.json.RedirectToNoteResponse;
import com.odde.doughnut.exceptions.NoAccessRightException;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import com.odde.doughnut.models.CircleModel;
import com.odde.doughnut.models.JsonViewer;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.testability.TestabilitySettings;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/circles")
class RestCircleController {
  private final ModelFactoryService modelFactoryService;
  private final CurrentUserFetcher currentUserFetcher;

  @Resource(name = "testabilitySettings")
  private final TestabilitySettings testabilitySettings;

  public RestCircleController(
      ModelFactoryService modelFactoryService,
      CurrentUserFetcher currentUserFetcher,
      TestabilitySettings testabilitySettings) {
    this.modelFactoryService = modelFactoryService;
    this.currentUserFetcher = currentUserFetcher;
    this.testabilitySettings = testabilitySettings;
  }

  @GetMapping("/{circle}")
  public CircleForUserView showCircle(@PathVariable("circle") Circle circle)
      throws NoAccessRightException {
    currentUserFetcher.getUser().getAuthorization().assertAuthorization(circle);
    JsonViewer jsonViewer = new JsonViewer(currentUserFetcher.getUser().getEntity());
    return jsonViewer.jsonCircleForUserView(circle);
  }

  @GetMapping("")
  public List<Circle> index() {
    UserModel user = currentUserFetcher.getUser();
    user.getAuthorization().assertLoggedIn();
    return user.getEntity().getCircles();
  }

  @PostMapping("")
  public Circle createCircle(@Valid Circle circle) {
    UserModel userModel = currentUserFetcher.getUser();
    CircleModel circleModel = modelFactoryService.toCircleModel(circle);
    circleModel.joinAndSave(userModel);
    return circle;
  }

  @PostMapping("/join")
  @Transactional
  public Circle joinCircle(@Valid CircleJoiningByInvitation circleJoiningByInvitation)
      throws BindException {
    CircleModel circleModel =
        modelFactoryService.findCircleByInvitationCode(
            circleJoiningByInvitation.getInvitationCode());
    if (circleModel == null) {
      BindingResult bindingResult =
          new BeanPropertyBindingResult(circleJoiningByInvitation, "circle");
      bindingResult.rejectValue("invitationCode", "error.error", "Does not match any circle");

      throw new BindException(bindingResult);
    }
    UserModel userModel = currentUserFetcher.getUser();
    if (userModel.getEntity().inCircle(circleModel.getEntity())) {
      BindingResult bindingResult =
          new BeanPropertyBindingResult(circleJoiningByInvitation, "circle");
      bindingResult.rejectValue("invitationCode", "error.error", "You are already in this circle");
      throw new BindException(bindingResult);
    }
    circleModel.joinAndSave(userModel);
    return circleModel.getEntity();
  }

  @PostMapping({"/{circle}/notebooks"})
  public RedirectToNoteResponse createNotebook(
      Circle circle, @Valid @ModelAttribute TextContent textContent) throws NoAccessRightException {
    UserModel user = currentUserFetcher.getUser();
    user.getAuthorization().assertLoggedIn();
    currentUserFetcher.getUser().getAuthorization().assertAuthorization(circle);
    Note note =
        circle
            .getOwnership()
            .createNotebook(
                user.getEntity(), textContent, testabilitySettings.getCurrentUTCTimestamp());
    modelFactoryService.noteRepository.save(note);
    return new RedirectToNoteResponse(note.getId());
  }
}
