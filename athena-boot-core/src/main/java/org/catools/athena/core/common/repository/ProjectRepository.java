package org.catools.athena.core.common.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.catools.athena.core.common.entity.Project;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Hidden
public interface ProjectRepository extends JpaRepository<Project, Long> {
  @Cacheable(value = "projectByCode", key = "#p0", condition = "#p0!=null", unless = "#result==null")
  Optional<Project> findByCode(String code);

  Optional<Project> findByCodeOrName(String code, String name);

  @Override
  <S extends Project> S save(S entity);
}
