package org.catools.athena.core.common.service;

import lombok.RequiredArgsConstructor;
import org.catools.athena.core.common.entity.Environment;
import org.catools.athena.core.common.mapper.CoreMapper;
import org.catools.athena.core.common.repository.EnvironmentRepository;
import org.catools.athena.core.common.repository.ProjectRepository;
import org.catools.athena.core.model.EnvironmentDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnvironmentServiceImpl implements EnvironmentService {

  private final ProjectRepository projectRepository;
  private final EnvironmentRepository environmentRepository;

  // Mappers
  private final CoreMapper coreMapper;

  @Override
  public Optional<EnvironmentDto> getByCode(final String code) {
    final Optional<Environment> environment = environmentRepository.findByCode(code);
    return environment.map(coreMapper::environmentToEnvironmentDto);
  }

  @Override
  public Optional<EnvironmentDto> getById(final Long id) {
    final Optional<Environment> environment = environmentRepository.findById(id);
    return environment.map(coreMapper::environmentToEnvironmentDto);
  }

  @Override
  public EnvironmentDto saveOrUpdate(final EnvironmentDto environmentDto) {
    final Environment environmentToSave = environmentRepository.findByCode(environmentDto.getCode())
        .map(env -> {
          env.setName(environmentDto.getName());
          env.setProject(projectRepository.findByCode(environmentDto.getProject()).orElse(null));
          return env;
        })
        .orElseGet(() -> coreMapper.environmentDtoToEnvironment(environmentDto));

    final Environment savedEnvironment = environmentRepository.saveAndFlush(environmentToSave);
    return coreMapper.environmentToEnvironmentDto(savedEnvironment);
  }
}