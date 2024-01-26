package org.catools.athena.git.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import org.catools.athena.core.model.MetadataDto;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;


@Data
@Accessors(chain = true)
public class CommitDto implements Serializable {

  private Long id;

  @NotNull(message = "The git commit 'parent count' must be provided.")
  private Integer parentCount;

  @NotBlank(message = "The git commit hash must be provided.")
  @Size(max = 50, message = "The git commit hash can be at most 50 character.")
  private String hash;

  @NotBlank(message = "The git commit short message must be provided.")
  @Size(max = 1000, message = "The git commit short message can be at most 1000 character.")
  private String shortMessage;

  @NotBlank(message = "The git commit full message must be provided.")
  @Size(max = 5000, message = "The git commit full message can be at most 5000 character.")
  private String fullMessage;

  @NotNull(message = "The git commit 'commit time' must be provided.")
  private Instant commitTime;

  @NotNull(message = "The git commit author must be provided.")
  private String author;

  @NotNull(message = "The git commit committer must be provided.")
  private String committer;

  @NotNull(message = "The git commit 'merged' must be provided.")
  private Boolean merged;

  @NotNull(message = "The git commit file changes must be provided.")
  @NotEmpty(message = "The git commit file changes must not be empty.")
  private Set<DiffEntryDto> diffEntries = new HashSet<>();

  @NotNull(message = "The git commit related branches must be provided.")
  @NotEmpty(message = "The git commit related branches must not be empty.")
  private Set<BranchDto> branches = new HashSet<>();

  private Set<MetadataDto> tags = new HashSet<>();

  private Set<MetadataDto> metadata = new HashSet<>();
}
