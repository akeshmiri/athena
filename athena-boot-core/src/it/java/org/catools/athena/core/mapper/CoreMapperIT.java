package org.catools.athena.core.mapper;

import org.catools.athena.AthenaSpringBootIT;
import org.catools.athena.core.builder.CoreBuilder;
import org.catools.athena.core.common.entity.Environment;
import org.catools.athena.core.common.entity.Project;
import org.catools.athena.core.common.entity.User;
import org.catools.athena.core.common.mapper.CoreMapper;
import org.catools.athena.core.common.service.EnvironmentService;
import org.catools.athena.core.common.service.ProjectService;
import org.catools.athena.core.common.service.UserService;
import org.catools.athena.core.model.EnvironmentDto;
import org.catools.athena.core.model.ProjectDto;
import org.catools.athena.core.model.UserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;


class CoreMapperIT extends AthenaSpringBootIT {
  private static ProjectDto projectDto;
  private static Project project;
  private static EnvironmentDto environmentDto;
  private static Environment environment;

  @Autowired
  CoreMapper coreMapper;

  @Autowired
  UserService athenaCoreService;

  @Autowired
  ProjectService projectService;

  @Autowired
  EnvironmentService environmentService;

  @BeforeAll
  public void beforeAll() {
    UserDto userDto = CoreBuilder.buildUserDto();
    userDto.setId(athenaCoreService.save(userDto).getId());

    projectDto = CoreBuilder.buildProjectDto();
    projectDto.setId(projectService.save(projectDto).getId());
    project = CoreBuilder.buildProject(projectDto);

    environmentDto = CoreBuilder.buildEnvironmentDto(projectDto.getCode());
    environmentDto.setId(environmentService.save(environmentDto).getId());
    environment = CoreBuilder.buildEnvironment(environmentDto, project);
  }

  @Test
  void projectDtoToProject() {
    final Project proj = coreMapper.projectDtoToProject(projectDto);
    assertThat(proj.getCode(), equalTo(projectDto.getCode()));
    assertThat(proj.getName(), equalTo(projectDto.getName()));
  }

  @Test
  void projectDtoToProject_shallReturnNullIfTheInputIsNull() {
    assertThat(coreMapper.projectDtoToProject(null), nullValue());
  }

  @Test
  void projectToProjectDto() {
    final ProjectDto projDto = coreMapper.projectToProjectDto(project);
    assertThat(project.getId(), equalTo(projDto.getId()));
    assertThat(project.getCode(), equalTo(projDto.getCode()));
    assertThat(project.getName(), equalTo(projDto.getName()));
  }

  @Test
  void projectToProjectDto_shallReturnNullIfTheInputIsNull() {
    assertThat(coreMapper.projectToProjectDto(null), nullValue());
  }

  @Test
  void environmentDtoToEnvironment() {
    final Environment env = coreMapper.environmentDtoToEnvironment(environmentDto);
    assertThat(env.getCode(), equalTo(environmentDto.getCode()));
    assertThat(env.getName(), equalTo(environmentDto.getName()));
    assertThat(env.getProject().getCode(), equalTo(environmentDto.getProject()));
  }

  @Test
  void environmentDtoToEnvironment_shallReturnNullIfTheInputIsNull() {
    assertThat(coreMapper.environmentDtoToEnvironment(null), nullValue());
  }

  @Test
  void environmentToEnvironmentDto() {
    final EnvironmentDto envDto = coreMapper.environmentToEnvironmentDto(environment);
    assertThat(environment.getId(), equalTo(envDto.getId()));
    assertThat(environment.getCode(), equalTo(envDto.getCode()));
    assertThat(environment.getName(), equalTo(envDto.getName()));
    assertThat(environment.getProject().getCode(), equalTo(envDto.getProject()));
  }

  @Test
  void environmentToEnvironmentDto_shallReturnNullIfTheInputIsNull() {
    assertThat(coreMapper.environmentToEnvironmentDto(null), nullValue());
  }

  @Test
  void userDtoToUser() {
    final UserDto userDto = new UserDto();
    userDto.setUsername("UserName");

    // when
    final User user = coreMapper.userDtoToUser(userDto);

    // then
    assertThat(userDto.getUsername(), equalTo(user.getUsername()));
  }

  @Test
  void userDtoToUser_shallReturnNullIfTheInputIsNull() {
    assertThat(coreMapper.userDtoToUser(null), nullValue());
  }

  @Test
  void userToUserDto() {
    final User user = new User(1L, "UserName");

    // when
    final UserDto actualDto = coreMapper.userToUserDto(user);

    // then
    assertThat(actualDto.getUsername(), equalTo(user.getUsername()));
  }

  @Test
  void userToUserDto_shallReturnNullIfTheInputIsNull() {
    assertThat(coreMapper.userToUserDto(null), nullValue());
  }
}