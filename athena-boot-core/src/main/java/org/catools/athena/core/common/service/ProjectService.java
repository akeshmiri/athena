package org.catools.athena.core.common.service;

import org.catools.athena.core.model.ProjectDto;

import java.util.Optional;

public interface ProjectService {
  Optional<ProjectDto> search(String keyword);

  Optional<ProjectDto> getById(Long id);

  ProjectDto save(ProjectDto entity);

  ProjectDto update(ProjectDto entity);
}
