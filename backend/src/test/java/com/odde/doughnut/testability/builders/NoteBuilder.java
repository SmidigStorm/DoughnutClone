package com.odde.doughnut.testability.builders;

import com.odde.doughnut.entities.Circle;
import com.odde.doughnut.entities.Link;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.Ownership;
import com.odde.doughnut.entities.ReviewSetting;
import com.odde.doughnut.entities.User;
import com.odde.doughnut.models.CircleModel;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.testability.EntityBuilder;
import com.odde.doughnut.testability.MakeMe;
import java.sql.Timestamp;
import org.apache.logging.log4j.util.Strings;

public class NoteBuilder extends EntityBuilder<Note> {
  static final TestObjectCounter titleCounter = new TestObjectCounter(n -> "title" + n);

  public NoteBuilder(Note note, MakeMe makeMe) {
    super(makeMe, note);
    if (Strings.isEmpty(note.getTitle())) title(titleCounter.generate());
    description("descrption");
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    createdAt(timestamp);
    updatedAt(timestamp);
    textContentUpdateAt(timestamp);
  }

  public NoteBuilder byUser(User user) {
    entity.setUser(user);
    return this;
  }

  public NoteBuilder byUser(UserModel userModel) {
    return byUser(userModel.getEntity());
  }

  public NoteBuilder under(Note parentNote) {
    entity.setParentNote(parentNote);
    byUser(parentNote.getUser());
    return this;
  }

  public NoteBuilder linkTo(Note referTo) {
    return linkTo(referTo, Link.LinkType.SPECIALIZE);
  }

  public NoteBuilder linkTo(Note referTo, Link.LinkType linkType) {
    makeMe.aLink().between(entity, referTo, linkType);
    return this;
  }

  public NoteBuilder inCircle(CircleModel circleModel) {
    return inCircle(circleModel.getEntity());
  }

  public NoteBuilder inCircle(Circle circle) {
    buildNotebookUnlessExist();
    entity.getNotebook().setOwnership(circle.getOwnership());
    return this;
  }

  @Override
  protected void beforeCreate(boolean needPersist) {
    if (entity.getUser() == null) {
      Note parent = entity.getParentNote();
      if (parent != null && parent.getUser() != null) {
        byUser(parent.getUser());
      } else {
        byUser(makeMe.aUser().please(needPersist));
      }
    }

    buildNotebookUnlessExist();
  }

  public NoteBuilder skipReview() {
    entity.getNoteAccessories().setSkipReview(true);
    return this;
  }

  public NoteBuilder cancelSkipReview() {
    entity.getNoteAccessories().setSkipReview(false);
    return this;
  }

  public NoteBuilder withNoDescription() {
    return description("");
  }

  public NoteBuilder title(String text) {
    entity.getTextContent().setTitle(text);
    return this;
  }

  public NoteBuilder description(String text) {
    entity.getTextContent().setDescription(text);
    return this;
  }

  public NoteBuilder with10Children() {
    for (int i = 0; i < 10; i++) {
      makeMe.aNote().under(entity).please();
    }
    return this;
  }

  public NoteBuilder rememberSpelling() {
    if (entity.getMasterReviewSetting() == null) {
      entity.setMasterReviewSetting(new ReviewSetting());
    }
    entity.getMasterReviewSetting().setRememberSpelling(true);
    return this;
  }

  public NoteBuilder createdAt(Timestamp timestamp) {
    entity.setCreatedAt(timestamp);
    return this;
  }

  public NoteBuilder updatedAt(Timestamp timestamp) {
    entity.getNoteAccessories().setUpdatedAt(timestamp);
    return this;
  }

  public NoteBuilder textContentUpdateAt(Timestamp timestamp) {
    entity.getTextContent().setUpdatedAt(timestamp);
    return this;
  }

  public NoteBuilder pictureUrl(String picture) {
    entity.getNoteAccessories().setPictureUrl(picture);
    return this;
  }

  public NoteBuilder useParentPicture() {
    entity.getNoteAccessories().setUseParentPicture(true);
    return this;
  }

  public NoteBuilder withNewlyUploadedPicture() {
    entity
        .getNoteAccessories()
        .setUploadPictureProxy(makeMe.anUploadedPicture().toMultiplePartFilePlease());
    return this;
  }

  public void withUploadedPicture() {
    entity.getNoteAccessories().setUploadPicture(makeMe.anImage().please());
  }

  private void buildNotebookUnlessExist() {
    if (entity.getNotebook() != null) {
      return;
    }
    Ownership ownership = null;
    if (entity.getUser() != null) {
      ownership = entity.getUser().getOwnership();
    }
    entity.buildNotebookForHeadNote(ownership, entity.getUser());
  }

  public NoteBuilder notebookOwnership(User user) {
    entity.getNotebook().setOwnership(user.getOwnership());
    return this;
  }

  public NoteBuilder softDeleted() {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    entity.setDeletedAt(timestamp);
    return this;
  }
}
