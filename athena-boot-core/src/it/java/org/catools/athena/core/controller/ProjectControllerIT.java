package org.catools.athena.core.controller;

import feign.TypedResponse;
import org.catools.athena.core.builder.CoreBuilder;
import org.catools.athena.core.model.ProjectDto;
import org.catools.athena.feign.FeignUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProjectControllerIT extends CoreControllerIT {

  @Test
  @Order(1)
  void saveShouldSaveProjectWhenValidDataProvided() {
    ProjectDto projectDto = CoreBuilder.buildProjectDto();
    TypedResponse<Void> response = projectFeignClient.save(projectDto);
    verifyProject(response, projectDto);
  }

  @Test
  @Order(10)
  void saveShallNotSaveSameEntityTwice() {
    ProjectDto projectDto = CoreBuilder.buildProjectDto().setCode(project.getCode()).setName(project.getName());
    TypedResponse<Void> response = projectFeignClient.save(projectDto);
    assertThat(response.status(), equalTo(208));
    Long id = FeignUtils.getIdFromLocationHeader(response);
    assertThat(id, equalTo(project.getId()));
    assertThat(response.body(), nullValue());
  }

  @Test
  @Order(3)
  void getProjectShallReturnProjectIfValidProjectCodeProvided() {
    TypedResponse<ProjectDto> response = projectFeignClient.search(projectDto.getCode());
    assertThat(response.status(), equalTo(200));
    FeignUtils.assertBodyEquals("Response is correct", response, """
        {
          "id" : 1,
          "code" : "ADM",
          "name" : "Administration"
        }""");
  }


  private void verifyProject(TypedResponse<Void> response, ProjectDto projectDto) {
    assertThat(response.status(), equalTo(201));
    Long id = FeignUtils.getIdFromLocationHeader(response);
    ProjectDto savedProject = projectFeignClient.getById(id).body();
    assertThat(savedProject, notNullValue());
    assertThat(savedProject.getCode(), equalTo(projectDto.getCode()));
    assertThat(savedProject.getName(), equalTo(projectDto.getName()));
  }
}