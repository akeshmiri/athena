package org.catools.athena.core.common.service;

import org.catools.athena.core.model.EnvironmentDto;

import java.util.Optional;

public interface EnvironmentService {
  Optional<EnvironmentDto> search(String keyword);

  Optional<EnvironmentDto> getById(Long id);

  EnvironmentDto save(EnvironmentDto entity);

  EnvironmentDto update(EnvironmentDto entity);
}
