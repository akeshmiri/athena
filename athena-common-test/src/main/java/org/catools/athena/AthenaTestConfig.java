package org.catools.athena;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.zaxxer.hikari.HikariDataSource;
import org.catools.athena.common.utils.JacksonUtil;
import org.catools.athena.core.feign.EnvironmentFeignClient;
import org.catools.athena.core.feign.ProjectFeignClient;
import org.catools.athena.core.feign.UserFeignClient;
import org.catools.athena.core.feign.VersionFeignClient;
import org.catools.athena.feign.FeignBuilder;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@ComponentScan({"org.catools.athena"})
@PropertySource("classpath:application.properties")
public class AthenaTestConfig {

  private static final String POSTGRES = "postgres";
  private static final String SERVICE_NAME = "athena-core";
  private static final int CORE_SERVICE_PORT = 8081;

  @Bean
  @ServiceConnection
  @SuppressWarnings("all")
  public DockerComposeContainer<?> athenaApi() {
    Path projectRoot = Path.of(".").toAbsolutePath().getParent().getParent().toAbsolutePath();
    return new DockerComposeContainer<>(new File(projectRoot + "/docker/core-compose.yml"))
        .withExposedService(POSTGRES, 5432)
        .withExposedService(SERVICE_NAME, CORE_SERVICE_PORT)
        .waitingFor(POSTGRES, new LogMessageWaitStrategy()
            .withRegEx(".*database system is ready to accept connections.*")
            .withTimes(1)
            .withStartupTimeout(Duration.of(60L, ChronoUnit.SECONDS)))
        .waitingFor(SERVICE_NAME, new LogMessageWaitStrategy()
            .withRegEx(".*Started AthenaCoreApplication.*")
            .withTimes(1)
            .withStartupTimeout(Duration.of(60L, ChronoUnit.SECONDS)));
  }

  @Bean
  @Primary
  public DataSource dataSource(DockerComposeContainer<?> athenaApi) {
    ContainerState postgres = athenaApi.getContainerByServiceName(POSTGRES).orElseThrow();
    String host = postgres.getHost();
    Integer port = postgres.getMappedPort(5432);

    var dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(String.format("jdbc:postgresql://%s:%s/athena", host, port));
    dataSource.setUsername(POSTGRES);
    dataSource.setPassword("password");
    dataSource.setDriverClassName("org.postgresql.Driver");

    return dataSource;
  }

  @Bean
  @Primary
  public ProjectFeignClient projectFeignClient(DockerComposeContainer<?> athenaApi) {
    return coreFeignBuilder(athenaApi, ProjectFeignClient.class);
  }

  @Bean
  @Primary
  public UserFeignClient userFeignClient(DockerComposeContainer<?> athenaApi) {
    return coreFeignBuilder(athenaApi, UserFeignClient.class);
  }

  @Bean
  @Primary
  public EnvironmentFeignClient environmentFeignClient(DockerComposeContainer<?> athenaApi) {
    return coreFeignBuilder(athenaApi, EnvironmentFeignClient.class);
  }

  @Bean
  @Primary
  public VersionFeignClient versionFeignClient(DockerComposeContainer<?> athenaApi) {
    return coreFeignBuilder(athenaApi, VersionFeignClient.class);
  }

  public <T> T coreFeignBuilder(DockerComposeContainer<?> athenaApi, Class<T> apiType) {
    ContainerState athenaCore = athenaApi.getContainerByServiceName(SERVICE_NAME).orElseThrow();

    return FeignBuilder.feignBuilder(apiType,
        JacksonUtil.objectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES),
        athenaCore.getHost(),
        athenaCore.getMappedPort(CORE_SERVICE_PORT));
  }
}
