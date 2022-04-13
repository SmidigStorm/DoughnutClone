package com.odde.doughnut.controllers;

import com.odde.doughnut.controllers.currentUser.CurrentUserFetcher;
import com.odde.doughnut.entities.User;
import com.odde.doughnut.entities.json.CurrentUserInfo;
import com.odde.doughnut.exceptions.NoAccessRightException;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import com.odde.doughnut.models.Authorization;
import java.security.Principal;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
record RestUserController(
    ModelFactoryService modelFactoryService, CurrentUserFetcher currentUserFetcher) {

  @PostMapping("")
  public User createUser(Principal principal, User user) {
    if (principal == null) Authorization.throwUserNotFound();
    user.setExternalIdentifier(principal.getName());
    modelFactoryService.userRepository.save(user);
    return user;
  }

  @GetMapping("")
  public User getUserProfile() {
    return currentUserFetcher.getUser().getEntity();
  }

  @PatchMapping("/{user}")
  public @Valid User updateUser(@Valid User user) throws NoAccessRightException {
    currentUserFetcher.getUser().getAuthorization().assertAuthorization(user);
    modelFactoryService.userRepository.save(user);
    return user;
  }

  @GetMapping("/current-user-info")
  public CurrentUserInfo currentUserInfo() {
    CurrentUserInfo currentUserInfo = new CurrentUserInfo();
    currentUserInfo.user = currentUserFetcher.getUser().getEntity();
    currentUserInfo.externalIdentifier = currentUserFetcher.getExternalIdentifier();
    return currentUserInfo;
  }
}
