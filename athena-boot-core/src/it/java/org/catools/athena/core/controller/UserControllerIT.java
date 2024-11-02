package org.catools.athena.core.controller;

import feign.TypedResponse;
import org.apache.logging.log4j.util.Strings;
import org.catools.athena.core.builder.CoreBuilder;
import org.catools.athena.core.model.UserAliasDto;
import org.catools.athena.core.model.UserDto;
import org.catools.athena.feign.FeignUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Optional;

import static org.catools.athena.common.utils.ResponseEntityUtils.ENTITY_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.testcontainers.utility.Base58.randomString;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerIT extends CoreControllerIT {

  @Test
  @Order(1)
  void saveShouldSaveUserIfAllFieldsAreProvided() {
    UserDto userDto = CoreBuilder.buildUserDto();
    TypedResponse<Void> response = userFeignClient.save(userDto);
    verifyUser(response, userDto);
  }

  @Test
  @Order(12)
  void updateShallUpdateUserIfRecordWithTheSameUsernameExists() {
    UserDto user1Dto = CoreBuilder.buildUserDto();
    TypedResponse<Void> saved = userFeignClient.save(user1Dto);

    UserDto user2Dto = CoreBuilder.buildUserDto().setId(Long.valueOf(saved.headers().get(ENTITY_ID).stream().findFirst().get())).setUsername(user1Dto.getUsername());
    TypedResponse<Void> response = userFeignClient.update(user2Dto);
    user2Dto.getAliases().addAll(user1Dto.getAliases());
    verifyUser(response, user2Dto);
  }

  @Test
  @Order(13)
  void updateShallUpdateUserIfRecordWithTheSameAliasExists() {
    UserDto userDto = CoreBuilder.buildUserDto();
    userDto.getAliases().add(this.userDto.getAliases().stream().findAny().orElseThrow());
    TypedResponse<Void> response = userFeignClient.update(userDto.setId(this.userDto.getId()));
    userDto.getAliases().addAll(this.userDto.getAliases());
    userDto.setUsername(this.userDto.getUsername());
    verifyUser(response, userDto);
  }

  @Test
  @Order(2)
  void getUserShallReturnEmptyBodyIfInvalidKeywordProvided() {
    TypedResponse<UserDto> response = userFeignClient.search(randomString(10));
    assertThat(response.status(), equalTo(204));
    assertThat(response.body(), nullValue());
  }

  @Test
  @Order(2)
  void getUserShallReturnEmptyBodyIfProvidedKeywordIsEmpty() {
    TypedResponse<UserDto> response = userFeignClient.search(Strings.EMPTY);
    assertThat(response.status(), equalTo(204));
    assertThat(response.body(), nullValue());
  }

  @Test
  @Order(2)
  void getUserShallReturnUserIfValidUsernameProvided() {
    TypedResponse<UserDto> response = userFeignClient.search(userDto.getUsername());
    assertThat(response.status(), equalTo(200));
    FeignUtils.assertBodyEquals("Response is correct", response, """
        {
           "id" : 1,
           "username" : "AKeshmiri",
           "aliases" : [ {
             "id" : 1,
             "alias" : "ak"
           } ]
         }""");
  }

  @Test
  @Order(2)
  void getUserShallReturnUserIfValidAliasProvided() {
    Optional<UserAliasDto> anyAlias = userDto.getAliases().stream().findAny();
    TypedResponse<UserDto> response = userFeignClient.search(anyAlias.orElseThrow().getAlias());
    assertThat(response.status(), equalTo(200));
    FeignUtils.assertBodyEquals("Response is correct", response, """
        {
           "id" : 1,
           "username" : "AKeshmiri",
           "aliases" : [ {
             "id" : 1,
             "alias" : "ak"
           } ]
        }""");
  }

  private void verifyUser(TypedResponse<Void> response, UserDto userDto) {
    assertThat(response.status(), equalTo(201));
    Long id = FeignUtils.getIdFromLocationHeader(response);
    UserDto savedUser = userFeignClient.getById(id).body();
    assertThat(savedUser, notNullValue());
    assertThat(savedUser.getUsername(), equalTo(userDto.getUsername()));
    UserAliasDto expectedAlias = userDto.getAliases().stream().findAny().orElseThrow();
    assertThat(savedUser.getAliases().size(), equalTo(userDto.getAliases().size()));

    Optional<UserAliasDto> actualAlias = savedUser.getAliases().stream().filter(a -> a.getAlias().equals(expectedAlias.getAlias())).findFirst();
    assertThat(actualAlias.isPresent(), equalTo(true));
  }
}