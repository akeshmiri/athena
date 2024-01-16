package org.catools.athena.rest.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.catools.athena.core.model.VersionDto;
import org.catools.athena.rest.common.utils.ResponseEntityUtils;
import org.catools.athena.rest.core.service.VersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

import static org.catools.athena.rest.core.controller.CoreDefinitions.VERSIONS_PATH;
import static org.catools.athena.rest.core.controller.CoreDefinitions.VERSION_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Tag(name = "Athena Version Rest API")
@RequestMapping(value = CoreDefinitions.ROOT_API, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VersionController {

    private final VersionService versionService;

    @GetMapping(VERSIONS_PATH)
    @Operation(
            summary = "Retrieve project versions",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
                    @ApiResponse(responseCode = "204", description = "No content to return")
            })
    public ResponseEntity<Set<VersionDto>> getVersions(
            @Parameter(name = "projectCode", description = "The code of the project to retrieve versions for")
            @RequestParam final String projectCode
    ) {
        return ResponseEntityUtils.okOrNoContent(versionService.getVersions(projectCode));
    }

    @GetMapping(VERSION_PATH)
    @Operation(
            summary = "Retrieve version by version code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
                    @ApiResponse(responseCode = "204", description = "No content to return")
            })
    public ResponseEntity<VersionDto> getVersionByCode(
            @Parameter(name = "versionCode", description = "The code of the version to retrieve")
            @RequestParam final String envCode
    ) {
        return ResponseEntityUtils.okOrNoContent(versionService.getVersionByCode(envCode));
    }

    @GetMapping(VERSION_PATH + "/{id}")
    @Operation(
            summary = "Retrieve version by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
                    @ApiResponse(responseCode = "204", description = "No content to return")
            })
    public ResponseEntity<VersionDto> getVersionById(
            @Parameter(name = "id", description = "The id of the version to retrieve")
            @PathVariable final Long id
    ) {
        return ResponseEntityUtils.okOrNoContent(versionService.getVersionById(id));
    }

    @PostMapping(VERSION_PATH)
    @Operation(
            summary = "Save version",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Version is created"),
                    @ApiResponse(responseCode = "208", description = "Version is already exists"),
                    @ApiResponse(responseCode = "400", description = "Failed to process request")
            })
    public ResponseEntity<Void> saveVersion(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The version to save")
            @Validated @RequestBody final VersionDto version
    ) {
        final Optional<VersionDto> versionByCode = versionService.getVersionByCode(version.getCode());
        if (versionByCode.isPresent()) {
            return ResponseEntityUtils.alreadyReported(VERSION_PATH, versionByCode.get().getId());
        }

        final VersionDto savedVersionDto = versionService.save(version);
        return ResponseEntityUtils.created(VERSION_PATH, savedVersionDto.getId());
    }
}
