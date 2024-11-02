package org.catools.athena.core.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.catools.athena.common.controlleradvice.ControllerErrorHandler;
import org.catools.athena.common.markers.IdRequired;
import org.catools.athena.common.utils.ResponseEntityUtils;
import org.catools.athena.core.common.service.VersionService;
import org.catools.athena.core.model.VersionDto;
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

import static org.catools.athena.core.rest.controller.VersionController.VERSION;

@RestController
@Tag(name = "Athena Version Rest API")
@RequestMapping(value = VERSION, produces = MediaType.APPLICATION_JSON_VALUE)
@Import(ControllerErrorHandler.class)
@RequiredArgsConstructor
public class VersionController {

  public static final String VERSION = "/version";

  private final VersionService versionService;

  @GetMapping
  @Operation(
      summary = "Retrieve version by version code or name",
      responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
          @ApiResponse(responseCode = "204", description = "No content to return")
      })
  public ResponseEntity<VersionDto> search(
      @Parameter(name = "keyword", description = "The code or name of the version to retrieve")
      @RequestParam final String keyword
  ) {
    return ResponseEntityUtils.okOrNoContent(versionService.search(keyword));
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Retrieve version by id",
      responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
          @ApiResponse(responseCode = "204", description = "No content to return")
      })
  public ResponseEntity<VersionDto> getById(
      @Parameter(name = "id", description = "The id of the version to retrieve")
      @PathVariable final Long id
  ) {
    return ResponseEntityUtils.okOrNoContent(versionService.getById(id));
  }

  @PostMapping
  @Operation(
      summary = "Save version or update the current one if any with the same code exists",
      responses = {
          @ApiResponse(responseCode = "201", description = "Version is created"),
          @ApiResponse(responseCode = "208", description = "Version already exists"),
          @ApiResponse(responseCode = "400", description = "Failed to process request"),
          @ApiResponse(responseCode = "409", description = "Version already exists")
      })
  public ResponseEntity<Void> save(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The version to save or update")
      @Validated @RequestBody final VersionDto version
  ) {
    try {
      final VersionDto savedVersionDto = versionService.save(version);
      return ResponseEntityUtils.created(VERSION, savedVersionDto.getId());
    } catch (DataIntegrityViolationException ex) {
      if (ex.getCause() instanceof ConstraintViolationException) {
        Optional<VersionDto> dbRecord = versionService.search(version.getCode());
        if (dbRecord.isPresent())
          return ResponseEntityUtils.alreadyReported(VERSION, dbRecord.get().getId());
      }
      return ResponseEntityUtils.conflicted();
    }
  }

  @PutMapping
  @Operation(
      summary = "Save version or update the current one if any with the same code exists",
      responses = {
          @ApiResponse(responseCode = "201", description = "Version is created"),
          @ApiResponse(responseCode = "208", description = "Version is already exists"),
          @ApiResponse(responseCode = "400", description = "Failed to process request")
      })
  public ResponseEntity<Void> update(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The version to save or update")
      @Validated(IdRequired.class) @RequestBody final VersionDto version
  ) {
    final VersionDto savedVersionDto = versionService.update(version);
    return ResponseEntityUtils.created(VERSION, savedVersionDto.getId());
  }
}
