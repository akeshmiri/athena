package org.catools.athena.core.common.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.catools.athena.core.common.entity.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Hidden
public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {
  Optional<AppVersion> findByCodeOrName(String code, String name);

  Optional<AppVersion> findByCode(String code);

}
