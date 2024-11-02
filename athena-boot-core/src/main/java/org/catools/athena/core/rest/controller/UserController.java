package org.catools.athena.core.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.catools.athena.common.controlleradvice.ControllerErrorHandler;
import org.catools.athena.common.markers.IdRequired;
import org.catools.athena.common.utils.ResponseEntityUtils;
import org.catools.athena.core.common.service.UserService;
import org.catools.athena.core.model.UserDto;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.catools.athena.core.rest.controller.UserController.USER;

@RestController
@Tag(name = "Athena User Rest API")
@RequestMapping(value = USER, produces = MediaType.APPLICATION_JSON_VALUE)
@Import(ControllerErrorHandler.class)
@RequiredArgsConstructor
public class UserController {

  public static final String USER = "/user";

  private final UserService userService;

  @GetMapping
  @Operation(
      summary = "Retrieve user who's username or any alias match the provided keyword",
      responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
          @ApiResponse(responseCode = "204", description = "No content to return")
      })
  public ResponseEntity<UserDto> search(
      @Parameter(name = "keyword", description = "The keyword to search user by")
      @RequestParam final String keyword
  ) {
    return ResponseEntityUtils.okOrNoContent(userService.search(keyword));
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Retrieve user by id",
      responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
          @ApiResponse(responseCode = "204", description = "No content to return")
      })
  public ResponseEntity<UserDto> getById(
      @Parameter(name = "id", description = "The id of the user to retrieve")
      @PathVariable final Long id
  ) {
    return ResponseEntityUtils.okOrNoContent(userService.getById(id));
  }

  @PostMapping
  @Operation(
      summary = "Save user or update the exist one if any with the same username or same alias exists",
      responses = {
          @ApiResponse(responseCode = "201", description = "User is created"),
          @ApiResponse(responseCode = "208", description = "User already exists"),
          @ApiResponse(responseCode = "400", description = "Failed to process request"),
          @ApiResponse(responseCode = "409", description = "User already exists")
      })
  public ResponseEntity<Void> save(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The user to save or update")
      @Validated @RequestBody final UserDto user
  ) {
    try {
      final UserDto savedUserDto = userService.save(user);
      return ResponseEntityUtils.created(USER, savedUserDto.getId());
    } catch (DataIntegrityViolationException ex) {
      if (ex.getCause() instanceof ConstraintViolationException) {
        Optional<UserDto> dbRecord = userService.getByUsername(user.getUsername());
        if (dbRecord.isPresent())
          return ResponseEntityUtils.alreadyReported(USER, dbRecord.get().getId());
      }
      return ResponseEntityUtils.conflicted();
    }
  }

  @PutMapping
  @Operation(
      summary = "Save user or update the exist one if any with the same username or same alias exists",
      responses = {
          @ApiResponse(responseCode = "201", description = "User is created"),
          @ApiResponse(responseCode = "400", description = "Failed to process request")
      })
  public ResponseEntity<Void> update(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The user to save or update")
      @Validated(IdRequired.class) @RequestBody final UserDto user
  ) {
    final UserDto savedUserDto = userService.update(user);
    return ResponseEntityUtils.created(USER, savedUserDto.getId());
  }
}
