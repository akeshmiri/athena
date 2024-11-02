package org.catools.athena.core.controller;

import feign.TypedResponse;
import org.apache.logging.log4j.util.Strings;
import org.catools.athena.core.builder.CoreBuilder;
import org.catools.athena.core.model.EnvironmentDto;
import org.catools.athena.feign.FeignUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.testcontainers.utility.Base58.randomString;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EnvironmentControllerIT extends CoreControllerIT {

  @Test
  @Order(1)
  void saveShouldSaveEnvironmentIfAllFieldsAreProvided() {
    TypedResponse<Void> response = environmentFeignClient.save(environmentDto);
    verifyEnvironment(response, environmentDto);
  }

  @Test
  @Order(2)
  void saveShallNotSaveSameEntityTwice() {
    EnvironmentDto environmentDto = CoreBuilder.buildEnvironmentDto(project.getCode()).setCode(this.environmentDto.getCode());
    TypedResponse<Void> response = environmentFeignClient.save(environmentDto);
    assertThat(response.status(), equalTo(208));
    Long id = FeignUtils.getIdFromLocationHeader(response);
    assertThat(id, equalTo(this.environmentDto.getId()));
    assertThat(response.body(), nullValue());
  }

  @Test
  @Order(2)
  void getEnvironmentShallReturnEnvironmentIfValidCodeProvided() {
    TypedResponse<EnvironmentDto> response = environmentFeignClient.search(environmentDto.getCode());
    assertThat(response.status(), equalTo(200));
    FeignUtils.assertBodyEquals("Response is correct", response, """
        {
          "id" : 1,
          "code" : "DEV1",
          "name" : "Development environment for Administration",
          "project" : "ADM"
        }""");
  }

  @Test
  @Order(2)
  void getEnvironmentShallReturnEmptyBodyIfInvalidCodeProvided() {
    TypedResponse<EnvironmentDto> response = environmentFeignClient.search(randomString(10));
    assertThat(response.status(), equalTo(204));
    assertThat(response.body(), nullValue());
  }

  @Test
  @Order(2)
  void getEnvironmentShallReturnEmptyBodyIfProvidedCodeIsEmpty() {
    TypedResponse<EnvironmentDto> response = environmentFeignClient.search(Strings.EMPTY);
    assertThat(response.status(), equalTo(204));
    assertThat(response.body(), nullValue());
  }

  private void verifyEnvironment(TypedResponse<Void> response, EnvironmentDto environmentDto) {
    assertThat(response.status(), equalTo(201));
    Long id = FeignUtils.getIdFromLocationHeader(response);
    EnvironmentDto savedEnv = environmentFeignClient.getById(id).body();
    assertThat(savedEnv, notNullValue());
    assertThat(savedEnv.getCode(), equalTo(environmentDto.getCode()));
    assertThat(savedEnv.getName(), equalTo(environmentDto.getName()));
  }
}