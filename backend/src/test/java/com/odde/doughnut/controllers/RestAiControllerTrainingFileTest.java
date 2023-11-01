package com.odde.doughnut.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.odde.doughnut.controllers.json.AiTrainingFile;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.testability.MakeMe;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.file.File;
import io.reactivex.Single;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:repository.xml"})
@Transactional
class RestAiControllerTrainingFileTest {

  @Autowired MakeMe makeMe;
  RestAiController controller;
  UserModel currentUser;
  @Mock private OpenAiApi openAiApi;

  @BeforeEach
  void setUp() {
    currentUser = makeMe.aUser().toModelPlease();
    controller = new RestAiController(openAiApi, makeMe.modelFactoryService, currentUser);
  }

  @Test
  void retrieveTrainingFiles() {

    OpenAiResponse<File> openAiResponse = getOpenAiFiles();

    Mockito.when(openAiApi.listFiles()).thenReturn(Single.just(openAiResponse));

    List<AiTrainingFile> aiTrainingFiles = controller.retrieveTrainingFiles();
    assertEquals(2, aiTrainingFiles.size());
  }

  @NotNull
  private OpenAiResponse<File> getOpenAiFiles() {
    File file = new File();
    file.setId("id");
    file.setObject("obj");
    file.setBytes(1L);
    file.setCreatedAt(1L);
    file.setFilename("filename");
    file.setPurpose("purpose");

    File file1 = new File();
    file.setId("id1");
    file.setObject("obj");
    file.setBytes(1L);
    file.setCreatedAt(2L);
    file.setFilename("filename1");
    file.setPurpose("purpose");

    OpenAiResponse<File> openAiResponse = new OpenAiResponse<>();
    openAiResponse.setData(List.of(file, file1));
    return openAiResponse;
  }
}