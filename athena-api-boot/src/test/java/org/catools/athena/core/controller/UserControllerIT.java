package org.catools.athena.core.controller;

import org.catools.athena.common.utils.ResponseEntityUtils;
import org.catools.athena.core.builder.CoreBuilder;
import org.catools.athena.core.model.UserAliasDto;
import org.catools.athena.core.model.UserDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testcontainers.utility.Base58.randomString;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerIT extends CoreControllerIT {

  @Test
  @Order(1)
  void saveShouldSaveUserIfAllFieldsAreProvided() {
    UserDto userDto = CoreBuilder.buildUserDto();
    ResponseEntity<Void> responseEntity = userController.saveOrUpdate(userDto);
    URI location = responseEntity.getHeaders().getLocation();
    assertThat(location, notNullValue());
    assertThat(responseEntity.getStatusCode().value(), equalTo(201));
    assertThat(responseEntity.getBody(), nullValue());

    Long id = ResponseEntityUtils.getId(location);
    assertThat(id, notNullValue());
    UserDto savedUser = userController.getById(id).getBody();
    assertThat(savedUser, notNullValue());
    assertThat(savedUser.getUsername(), equalTo(userDto.getUsername()));
    UserAliasDto expectedAlias = userDto.getAliases().stream().findAny().get();
    assertThat(savedUser.getAliases().size(), equalTo(userDto.getAliases().size()));

    Optional<UserAliasDto> actualAlias = savedUser.getAliases().stream().filter(a -> a.getAlias().equals(expectedAlias.getAlias())).findFirst();
    assertThat(actualAlias.isPresent(), equalTo(true));
  }

  @Test
  @Order(2)
  void saveShallNotSaveSameUserTwice() {
    ResponseEntity<Void> responseEntity = userController.saveOrUpdate(USER_DTO);
    URI location = responseEntity.getHeaders().getLocation();
    assertThat(location, notNullValue());
    assertThat(responseEntity.getStatusCode().value(), equalTo(201));
    assertThat(responseEntity.getBody(), nullValue());
  }

  @Test
  @Order(2)
  void getUserShallReturnEmptyBodyIfInvalidKeywordProvided() {
    ResponseEntity<UserDto> response = userController.search(randomString(10));
    assertThat(response.getStatusCode().value(), equalTo(204));
    assertThat(response.getBody(), nullValue());
  }

  @Test
  @Order(2)
  void getUserShallReturnEmptyBodyIfProvidedKeywordIsNull() {
    ResponseEntity<UserDto> response = userController.search(null);
    assertThat(response.getStatusCode().value(), equalTo(204));
    assertThat(response.getBody(), nullValue());
  }

  @Test
  @Order(2)
  void getUserShallReturnUserIfValidUsernameProvided() {
    ResponseEntity<UserDto> response = userController.search(USER_DTO.getUsername());
    assertThat(response.getStatusCode().value(), equalTo(200));
    assertThat(response.getBody(), notNullValue());
    assertThat(response.getBody().getUsername(), equalTo(USER_DTO.getUsername()));
  }

  @Test
  @Order(2)
  void getUserShallReturnUserIfValidAliasProvided() {
    Optional<UserAliasDto> anyAlias = USER_DTO.getAliases().stream().findAny();
    assertThat(anyAlias.isPresent(), equalTo(true));

    ResponseEntity<UserDto> response = userController.search(anyAlias.get().getAlias());
    assertThat(response.getStatusCode().value(), equalTo(200));
    assertThat(response.getBody(), notNullValue());
    assertThat(response.getBody().getUsername(), equalTo(USER_DTO.getUsername()));
  }

}