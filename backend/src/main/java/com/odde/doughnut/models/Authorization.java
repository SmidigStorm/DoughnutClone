package com.odde.doughnut.models;

import com.odde.doughnut.entities.*;
import com.odde.doughnut.exceptions.NoAccessRightException;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public record Authorization(User user, ModelFactoryService modelFactoryService) {

  public void assertAuthorization(Note note) throws NoAccessRightException {
    assertLoggedIn();
    if (!hasFullAuthority(note)) {
      throw new NoAccessRightException();
    }
  }

  public boolean hasFullAuthority(Note note) {
    return hasFullAuthority(note.getNotebook());
  }

  public boolean hasFullAuthority(Notebook notebook) {
    if (user == null) return false;
    return user.owns(notebook);
  }

  public boolean hasReferenceAuthority(Note note) {
    if (user == null) return false;
    return user.canReferTo(note.getNotebook());
  }

  public void assertReadAuthorization(Note note) throws NoAccessRightException {
    assertReadAuthorization(note.getNotebook());
  }

  public void assertReadAuthorization(Notebook notebook) throws NoAccessRightException {
    if (hasReferenceAuthority(notebook.getHeadNote())) {
      return;
    }
    if (modelFactoryService.bazaarNotebookRepository.findByNotebook(notebook) != null) {
      return;
    }
    assertLoggedIn();
    throw new NoAccessRightException();
  }

  public void assertAuthorization(Notebook notebook) throws NoAccessRightException {
    if (!hasFullAuthority(notebook)) {
      throw new NoAccessRightException();
    }
  }

  public void assertAuthorization(Circle circle) throws NoAccessRightException {
    assertLoggedIn();
    if (user == null || !user.inCircle(circle)) {
      throw new NoAccessRightException();
    }
  }

  public void assertAuthorization(Subscription subscription) throws NoAccessRightException {
    if (subscription.getUser() != user) {
      throw new NoAccessRightException();
    }
  }

  public void assertAuthorization(User user) throws NoAccessRightException {
    if (!this.user.getId().equals(user.getId())) {
      throw new NoAccessRightException();
    }
  }

  public void assertAuthorization(Link link) throws NoAccessRightException {
    if (link.getUser().getId() != user.getId()) {
      throw new NoAccessRightException();
    }
  }

  public void assertDeveloperAuthorization() throws NoAccessRightException {
    if (!isDeveloper()) {
      throw new NoAccessRightException();
    }
  }

  private static final List<String> allowUsers =
      Arrays.asList("Terry", "t-machu", "Developer", "Yeong Sheng");

  public boolean isDeveloper() {
    if (user == null) return false;
    return allowUsers.contains(user.getName());
  }

  public void assertLoggedIn() {
    if (user == null) {
      throwUserNotFound();
    }
  }

  public static void throwUserNotFound() {
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User Not Found");
  }

  public void assertAuthorization(ReviewPoint reviewPoint) {}
}
