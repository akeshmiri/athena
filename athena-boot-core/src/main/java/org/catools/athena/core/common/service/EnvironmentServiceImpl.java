package org.catools.athena.core.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.catools.athena.common.exception.RecordNotFoundException;
import org.catools.athena.core.common.entity.Environment;
import org.catools.athena.core.common.mapper.CoreMapper;
import org.catools.athena.core.common.repository.EnvironmentRepository;
import org.catools.athena.core.common.repository.ProjectRepository;
import org.catools.athena.core.model.EnvironmentDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnvironmentServiceImpl implements EnvironmentService {

  private final ProjectRepository projectRepository;
  private final EnvironmentRepository environmentRepository;

  // Mappers
  private final CoreMapper coreMapper;

  @Override
  @Transactional
  public Optional<EnvironmentDto> search(final String keyword) {
    log.info("Search for entity by keyword: {}", keyword);
    final Optional<Environment> environment = environmentRepository.findByCodeOrName(keyword, keyword);
    return environment.map(coreMapper::environmentToEnvironmentDto);
  }

  @Override
  @Transactional
  public Optional<EnvironmentDto> getById(final Long id) {
    log.info("Search for entity by id: {}", id);
    final Optional<Environment> environment = environmentRepository.findById(id);
    return environment.map(coreMapper::environmentToEnvironmentDto);
  }

  @Override
  @Transactional
  public EnvironmentDto save(final EnvironmentDto entity) {
    log.info("Saving entity: {}", entity);
    final Environment environmentToSave = coreMapper.environmentDtoToEnvironment(entity);
    final Environment savedEnvironment = environmentRepository.saveAndFlush(environmentToSave);
    return coreMapper.environmentToEnvironmentDto(savedEnvironment);
  }

  @Override
  @Transactional
  public EnvironmentDto update(final EnvironmentDto entity) {
    log.info("Update entity: {}", entity);
    final Environment environmentToSave = environmentRepository.findById(entity.getId())
        .map(env -> {
          env.setCode(entity.getCode());
          env.setName(entity.getName());
          env.setProject(projectRepository.findByCode(entity.getProject()).orElse(null));
          return env;
        })
        .orElseThrow(() -> new RecordNotFoundException("environment", "id", entity.getId()));

    final Environment savedEnvironment = environmentRepository.saveAndFlush(environmentToSave);
    return coreMapper.environmentToEnvironmentDto(savedEnvironment);
  }
}