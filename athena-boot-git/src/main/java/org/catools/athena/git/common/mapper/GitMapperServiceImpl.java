package org.catools.athena.git.common.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.catools.athena.common.exception.RecordNotFoundException;
import org.catools.athena.core.feign.UserFeignClient;
import org.catools.athena.core.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@SuppressWarnings("unused")
public class GitMapperServiceImpl implements GitMapperService {

  @Autowired
  private UserFeignClient userFeignClient;

  /**
   * Get user id by username
   *
   * @param username
   */
  @Override
  public Long getUserId(String username) {
    if (StringUtils.isBlank(username)) return null;
    return Optional.ofNullable(userFeignClient.search(username).body())
        .map(UserDto::getId)
        .orElseThrow(() -> new RecordNotFoundException("user", "username", username));
  }

  /**
   * Get username by Id
   *
   * @param id
   */
  @Override
  public String getUsername(Long id) {
    if (id == null) return null;
    return Optional.ofNullable(userFeignClient.getById(id).body())
        .map(UserDto::getUsername)
        .orElseThrow(() -> new RecordNotFoundException("user", "id", id));
  }
}
