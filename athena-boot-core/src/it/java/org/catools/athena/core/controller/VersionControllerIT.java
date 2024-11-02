package org.catools.athena.core.controller;

import feign.TypedResponse;
import org.apache.logging.log4j.util.Strings;
import org.catools.athena.core.builder.CoreBuilder;
import org.catools.athena.core.model.VersionDto;
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
class VersionControllerIT extends CoreControllerIT {

  @Test
  @Order(1)
  void saveShouldSaveVersionIfAllFieldsAreProvided() {
    VersionDto versionDto = CoreBuilder.buildVersionDto(projectDto.getCode());
    TypedResponse<Void> response = versionFeignClient.save(versionDto);
    verifyVersion(response, 201, versionDto);
  }

  @Test
  @Order(2)
  void saveShallNotSaveSameEntityTwice() {
    VersionDto version1 = new VersionDto().setCode(versionDto.getCode()).setName(versionDto.getName()).setProject(versionDto.getProject());
    TypedResponse<Void> response = versionFeignClient.save(version1);
    assertThat(response.status(), equalTo(208));
    Long id = FeignUtils.getIdFromLocationHeader(response);
    assertThat(id, equalTo(versionDto.getId()));
    assertThat(response.body(), nullValue());
  }

  @Test
  @Order(2)
  void getVersionShallReturnVersionIfValidCodeProvided() {
    TypedResponse<VersionDto> response = versionFeignClient.search(versionDto.getCode());
    assertThat(response.status(), equalTo(200));
    FeignUtils.assertBodyEquals("Response is correct", response, """
         {
            "id" : 1,
            "code" : "1.01",
            "name" : "1.0 version for Administration",
            "project" : "ADM"
        }""");
  }

  @Test
  @Order(2)
  void getVersionShallReturnEmptyBodyIfInvalidCodeProvided() {
    TypedResponse<VersionDto> response = versionFeignClient.search(randomString(10));
    assertThat(response.status(), equalTo(204));
    assertThat(response.body(), nullValue());
  }

  @Test
  @Order(2)
  void getVersionShallReturnEmptyBodyIfProvidedCodeIsEmpty() {
    TypedResponse<VersionDto> response = versionFeignClient.search(Strings.EMPTY);
    assertThat(response.status(), equalTo(204));
    assertThat(response.body(), nullValue());
  }


  private void verifyVersion(TypedResponse<Void> response, int status, VersionDto versionDto) {
    assertThat(response.status(), equalTo(status));
    Long id = FeignUtils.getIdFromLocationHeader(response);
    VersionDto savedVersion = versionFeignClient.getById(id).body();
    assertThat(savedVersion, notNullValue());
    assertThat(savedVersion.getName(), equalTo(versionDto.getName()));
    assertThat(savedVersion.getCode(), equalTo(versionDto.getCode()));
    assertThat(savedVersion.getProject(), equalTo(versionDto.getProject()));
  }
}