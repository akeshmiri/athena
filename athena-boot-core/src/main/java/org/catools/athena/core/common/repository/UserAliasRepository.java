package org.catools.athena.core.common.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.catools.athena.core.common.entity.UserAlias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Hidden
public interface UserAliasRepository extends JpaRepository<UserAlias, Long> {

  Optional<UserAlias> findByAliasIgnoreCase(String alias);

}
