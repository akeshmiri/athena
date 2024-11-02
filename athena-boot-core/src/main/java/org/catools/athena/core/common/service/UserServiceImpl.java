package org.catools.athena.core.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.catools.athena.core.common.entity.User;
import org.catools.athena.core.common.mapper.CoreMapper;
import org.catools.athena.core.common.repository.UserAliasRepository;
import org.catools.athena.core.common.repository.UserRepository;
import org.catools.athena.core.model.UserDto;
import org.catools.athena.core.utils.UserPersistentHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserPersistentHelper userPersistentHelper;

  private final UserRepository userRepository;

  private final UserAliasRepository userAliasRepository;

  // Mappers
  private final CoreMapper coreMapper;

  @Override
  @Transactional
  public Optional<UserDto> getById(final Long id) {
    log.info("Search for user by id: {}", id);
    final Optional<User> user = userRepository.findById(id);
    return user.map(coreMapper::userToUserDto);
  }

  @Override
  @Transactional
  public Optional<UserDto> getByUsername(String username) {
    log.info("Search for user by id: username: {}", username);
    return userRepository.findByUsernameIgnoreCase(username).map(coreMapper::userToUserDto);
  }

  @Override
  @Transactional
  public Optional<UserDto> search(final String keyword) {
    log.info("Search for user by username or alias: {}", keyword);
    return userRepository.findByKeywords(Set.of(keyword.toLowerCase())).map(coreMapper::userToUserDto);
  }

  @Override
  @Transactional
  public UserDto save(final UserDto entity) {
    log.debug("Saving user: {}", entity);
    User userToSave = coreMapper.userDtoToUser(entity);
    final User savedUser = userPersistentHelper.save(userToSave);
    return coreMapper.userToUserDto(savedUser);
  }

  @Override
  @Transactional
  public UserDto update(final UserDto entity) {
    log.debug("Updating user: {}", entity);
    User userToSave = coreMapper.userDtoToUser(entity);
    final User savedUser = userPersistentHelper.update(userToSave);
    return coreMapper.userToUserDto(savedUser);
  }
}