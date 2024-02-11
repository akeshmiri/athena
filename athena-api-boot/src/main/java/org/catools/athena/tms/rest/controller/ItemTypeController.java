package org.catools.athena.tms.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.catools.athena.common.utils.ResponseEntityUtils;
import org.catools.athena.core.common.config.CorePathDefinitions;
import org.catools.athena.tms.common.service.ItemTypeService;
import org.catools.athena.tms.model.ItemTypeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.catools.athena.tms.common.config.TmsPathDefinitions.TMS_ITEM;
import static org.catools.athena.tms.common.config.TmsPathDefinitions.TMS_ITEM_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Athena Task Management System - Item API")
@RestController
@RequestMapping(value = CorePathDefinitions.ROOT_API, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ItemTypeController {

  private final ItemTypeService itemTypeService;

  @GetMapping(TMS_ITEM_TYPE + "/{id}")
  @Operation(
      summary = "Retrieve item type by id",
      responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
          @ApiResponse(responseCode = "204", description = "No content to return")
      })
  public ResponseEntity<ItemTypeDto> getById(
      @Parameter(name = "id", description = "The id of item type to retrieve")
      @PathVariable final Long id
  ) {
    return ResponseEntityUtils.okOrNoContent(itemTypeService.getById(id));
  }

  @GetMapping(TMS_ITEM_TYPE)
  @Operation(
      summary = "Retrieve item type by code",
      responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
          @ApiResponse(responseCode = "204", description = "No content to return")
      })
  public ResponseEntity<ItemTypeDto> getByCode(
      @Parameter(name = "code", description = "The code of the item type to retrieve")
      @RequestParam final String code
  ) {
    return ResponseEntityUtils.okOrNoContent(itemTypeService.getByCode(code));
  }

  @PostMapping(TMS_ITEM_TYPE)
  @Operation(
      summary = "Save item type or update the current one if any with the same code exists",
      responses = {
          @ApiResponse(responseCode = "201", description = "Api spec is created"),
          @ApiResponse(responseCode = "400", description = "Failed to process request")
      })
  public ResponseEntity<Void> saveOrUpdate(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The item type to save or update")
      @Validated @RequestBody final ItemTypeDto itemTypeDto
  ) {
    final ItemTypeDto savedRecord = itemTypeService.saveOrUpdate(itemTypeDto);
    return ResponseEntityUtils.created(TMS_ITEM, savedRecord.getId());
  }
}
