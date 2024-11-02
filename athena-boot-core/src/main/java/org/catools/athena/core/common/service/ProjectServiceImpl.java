package org.catools.athena.core.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.catools.athena.common.exception.RecordNotFoundException;
import org.catools.athena.core.common.entity.Project;
import org.catools.athena.core.common.mapper.CoreMapper;
import org.catools.athena.core.common.repository.ProjectRepository;
import org.catools.athena.core.model.ProjectDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;

  // Mappers
  private final CoreMapper coreMapper;

  @Override
  @Transactional
  public Optional<ProjectDto> search(final String keyword) {
    final Optional<Project> project = projectRepository.findByCodeOrName(keyword, keyword);
    return project.map(coreMapper::projectToProjectDto);
  }

  @Override
  @Transactional
  public Optional<ProjectDto> getById(final Long id) {
    final Optional<Project> project = projectRepository.findById(id);
    return project.map(coreMapper::projectToProjectDto);
  }

  @Override
  @Transactional
  public ProjectDto save(final ProjectDto entity) {
    log.debug("Saving entity: {}", entity);
    final Project projectToSave = coreMapper.projectDtoToProject(entity);
    final Project savedProject = projectRepository.saveAndFlush(projectToSave);
    return coreMapper.projectToProjectDto(savedProject);
  }

  @Override
  @Transactional
  public ProjectDto update(final ProjectDto entity) {
    log.debug("Updating entity: {}", entity);
    final Project projectToSave = projectRepository.findById(entity.getId())
        .map(p -> {
          p.setCode(entity.getCode());
          p.setName(entity.getName());
          return p;
        })
        .orElseThrow(() -> new RecordNotFoundException("project", "id", entity.getId()));

    final Project savedProject = projectRepository.saveAndFlush(projectToSave);
    return coreMapper.projectToProjectDto(savedProject);
  }
}