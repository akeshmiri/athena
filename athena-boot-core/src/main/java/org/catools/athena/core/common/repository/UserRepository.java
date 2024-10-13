package org.catools.athena.core.common.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.catools.athena.core.common.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Hidden
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

  @Cacheable(value = "userByUsername", key = "#p0", condition = "#p0!=null", unless = "#result==null")
  Optional<User> findByUsernameIgnoreCase(String username);

  @Override
  <S extends User> S save(S entity);

}
