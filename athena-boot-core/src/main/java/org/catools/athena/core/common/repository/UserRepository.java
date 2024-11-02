package org.catools.athena.core.common.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.catools.athena.core.common.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

@Hidden
public interface UserRepository extends JpaRepository<User, Long> {

  @Cacheable(value = "userByUsername", key = "#p0", condition = "#p0!=null", unless = "#result==null")
  Optional<User> findByUsernameIgnoreCase(String username);

  @Cacheable(value = "userByUsernameOrAlias", key = "#p0", condition = "#p0!=null", unless = "#result==null")
  @Query("SELECT u FROM User u JOIN FETCH u.aliases ua WHERE lower(u.username) in (:keywords) or ua.alias in :keywords")
  Optional<User> findByKeywords(@Param("keywords") Set<String> keywords);

  @Override
  <S extends User> S save(S entity);

}
