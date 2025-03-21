package org.catools.athena.git.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.catools.athena.common.utils.ResponseEntityUtils;
import org.catools.athena.git.common.service.GitRepositoryService;
import org.catools.athena.git.model.GitRepositoryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Tag(name = "Athena Git Repository Rest API")
@RequestMapping(path = GitRepositoryController.REPOSITORY, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GitRepositoryController {
  public static final String REPOSITORY = "/repo";

  private final GitRepositoryService repositoryService;

  @GetMapping
  @Operation(
      summary = "Retrieve repository where keyword can be either repository name or url",
      responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
          @ApiResponse(responseCode = "204", description = "No content to return")
      })
  public ResponseEntity<GitRepositoryDto> search(
      @Parameter(name = "keyword", description = "The repository name or url to search for")
      @RequestParam final String keyword
  ) {
    return ResponseEntityUtils.okOrNoContent(repositoryService.findByNameOrUrl(keyword));
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Retrieve repository by id",
      responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
          @ApiResponse(responseCode = "204", description = "No content to return")
      })
  public ResponseEntity<GitRepositoryDto> getById(
      @Parameter(name = "id", description = "The id of the repository to retrieve")
      @PathVariable final Long id
  ) {
    return ResponseEntityUtils.okOrNoContent(repositoryService.getById(id));
  }

  @PostMapping
  @Operation(
      summary = "Save repository or update the current one if any with the same name or url exists",
      responses = {
          @ApiResponse(responseCode = "201", description = "Repository is created"),
          @ApiResponse(responseCode = "400", description = "Failed to process request")
      })
  public ResponseEntity<Void> saveOrUpdate(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The repository to save or update")
      @Validated @RequestBody final GitRepositoryDto repository
  ) {
    final GitRepositoryDto savedGitRepositoryDto = repositoryService.saveOrUpdate(repository);
    return ResponseEntityUtils.created(REPOSITORY, savedGitRepositoryDto.getId());
  }
}
