package org.catools.athena.core.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.catools.athena.common.controlleradvice.ControllerErrorHandler;
import org.catools.athena.common.markers.IdRequired;
import org.catools.athena.common.utils.ResponseEntityUtils;
import org.catools.athena.core.common.service.EnvironmentService;
import org.catools.athena.core.model.EnvironmentDto;
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

@RestController
@Tag(name = "Athena Environment Rest API")
@RequestMapping(value = EnvironmentController.ENVIRONMENT, produces = MediaType.APPLICATION_JSON_VALUE)
@Import(ControllerErrorHandler.class)
@RequiredArgsConstructor
public class EnvironmentController {

  public static final String ENVIRONMENT = "/environment";

  private final EnvironmentService environmentService;

  @GetMapping
  @Operation(
      summary = "Retrieve environment by environment code or name",
      responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
          @ApiResponse(responseCode = "204", description = "No content to return")
      })
  public ResponseEntity<EnvironmentDto> search(
      @Parameter(name = "keyword", description = "The code or name of the environment to retrieve")
      @RequestParam final String keyword
  ) {
    return ResponseEntityUtils.okOrNoContent(environmentService.search(keyword));
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Retrieve environment by id",
      responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
          @ApiResponse(responseCode = "204", description = "No content to return")
      })
  public ResponseEntity<EnvironmentDto> getById(
      @Parameter(name = "id", description = "The id of the environment to retrieve")
      @PathVariable final Long id
  ) {
    return ResponseEntityUtils.okOrNoContent(environmentService.getById(id));
  }

  @PostMapping
  @Operation(
      summary = "Save environment",
      responses = {
          @ApiResponse(responseCode = "201", description = "Environment is created"),
          @ApiResponse(responseCode = "208", description = "Environment already exists"),
          @ApiResponse(responseCode = "400", description = "Failed to process request"),
          @ApiResponse(responseCode = "409", description = "Environment already exists")
      })
  public ResponseEntity<Void> save(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The environment to save or update")
      @Validated @RequestBody final EnvironmentDto environment
  ) {
    try {
      final EnvironmentDto savedEnvironmentDto = environmentService.save(environment);
      return ResponseEntityUtils.created(ENVIRONMENT, savedEnvironmentDto.getId());
    } catch (DataIntegrityViolationException ex) {
      if (ex.getCause() instanceof ConstraintViolationException) {
        Optional<EnvironmentDto> dbRecord = environmentService.search(environment.getCode());
        if (dbRecord.isPresent())
          return ResponseEntityUtils.alreadyReported(ENVIRONMENT, dbRecord.get().getId());
      }
      return ResponseEntityUtils.conflicted();
    }
  }

  @PutMapping
  @Operation(
      summary = "Update environment if any with the same code exists or throw exception",
      responses = {
          @ApiResponse(responseCode = "200", description = "Environment is created"),
          @ApiResponse(responseCode = "400", description = "Failed to process request")
      })
  public ResponseEntity<Void> update(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The environment to save or update")
      @Validated(IdRequired.class) @RequestBody final EnvironmentDto environment
  ) {
    final EnvironmentDto savedEnvironmentDto = environmentService.update(environment);
    return ResponseEntityUtils.updated(ENVIRONMENT, savedEnvironmentDto.getId());
  }
}
