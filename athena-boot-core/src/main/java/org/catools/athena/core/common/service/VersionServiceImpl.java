package org.catools.athena.core.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.catools.athena.common.exception.EntityNotFoundException;
import org.catools.athena.common.exception.RecordNotFoundException;
import org.catools.athena.core.common.entity.AppVersion;
import org.catools.athena.core.common.entity.Project;
import org.catools.athena.core.common.mapper.CoreMapper;
import org.catools.athena.core.common.repository.AppVersionRepository;
import org.catools.athena.core.common.repository.ProjectRepository;
import org.catools.athena.core.model.VersionDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VersionServiceImpl implements VersionService {

  private final ProjectRepository projectRepository;
  private final AppVersionRepository appVersionRepository;

  private final CoreMapper coreMapper;

  @Override
  @Transactional
  public Optional<VersionDto> search(String keyword) {
    final Optional<AppVersion> version = appVersionRepository.findByCodeOrName(keyword, keyword);
    return version.map(coreMapper::versionToVersionDto);
  }

  @Override
  @Transactional
  public Optional<VersionDto> getById(Long id) {
    final Optional<AppVersion> version = appVersionRepository.findById(id);
    return version.map(coreMapper::versionToVersionDto);
  }

  @Override
  @Transactional
  public VersionDto save(VersionDto entity) {
    log.debug("Saving entity: {}", entity);
    final AppVersion appVersionToSave = coreMapper.versionDtoToVersion(entity);
    final AppVersion savedAppVersion = appVersionRepository.saveAndFlush(appVersionToSave);
    return coreMapper.versionToVersionDto(savedAppVersion);
  }

  @Override
  @Transactional
  public VersionDto update(final VersionDto entity) {
    log.debug("Saving entity: {}", entity);
    Project project = projectRepository.findByCode(entity.getProject()).orElseThrow(() -> new EntityNotFoundException("project", entity.getProject()));
    final AppVersion appVersionToSave = appVersionRepository.findById(entity.getId()).map(ver -> {
          ver.setName(entity.getName());
          ver.setProject(project);
          return ver;
        })
        .orElseThrow(() -> new RecordNotFoundException("version", "id", entity.getId()));

    final AppVersion savedAppVersion = appVersionRepository.saveAndFlush(appVersionToSave);
    return coreMapper.versionToVersionDto(savedAppVersion);
  }
}
