package org.catools.athena.core.common.service;

import org.catools.athena.core.model.UserDto;

import java.util.Optional;

public interface UserService {
  Optional<UserDto> getById(Long id);

  Optional<UserDto> getByUsername(String username);

  Optional<UserDto> search(String keyword);

  UserDto save(UserDto entity);

  UserDto update(UserDto entity);
}
