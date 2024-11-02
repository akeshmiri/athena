package org.catools.athena.core.common.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.catools.athena.core.common.entity.AppVersion;
import org.catools.athena.core.common.entity.Environment;
import org.catools.athena.core.common.entity.Project;
import org.catools.athena.core.common.entity.User;
import org.catools.athena.core.common.repository.AppVersionRepository;
import org.catools.athena.core.common.repository.EnvironmentRepository;
import org.catools.athena.core.common.repository.ProjectRepository;
import org.catools.athena.core.common.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoreMapperServiceImpl implements CoreMapperService {

  private final EnvironmentRepository environmentRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final AppVersionRepository appVersionRepository;

  @SuppressWarnings("notused")
  @Override
  public Project getProject(String keyword) {
    if (StringUtils.isBlank(keyword)) return null;
    return projectRepository.findByCode(keyword).orElse(null);
  }

  @Override
  public Environment getEnvironment(String keyword) {
    if (StringUtils.isBlank(keyword)) return null;
    return environmentRepository.findByCodeOrName(keyword, keyword).orElse(null);
  }

  @Override
  public User search(String keyword) {
    if (StringUtils.isBlank(keyword)) return null;
    return userRepository.findByKeywords(Set.of(keyword.toLowerCase())).orElse(null);
  }

  @Override
  public AppVersion getVersion(String keyword) {
    if (StringUtils.isBlank(keyword)) return null;
    return appVersionRepository.findByCodeOrName(keyword, keyword).orElse(null);
  }


  @Override
  public Set<String> getVersionCodesFromVersions(Set<AppVersion> appVersions) {
    return appVersions.stream().map(AppVersion::getCode).collect(Collectors.toSet());
  }

}
