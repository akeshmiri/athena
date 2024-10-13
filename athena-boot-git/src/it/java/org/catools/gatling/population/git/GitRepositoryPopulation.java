package org.catools.gatling.population.git;

import io.gatling.http.client.body.string.StringRequestBody;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import org.catools.athena.common.utils.JacksonUtil;
import org.catools.athena.git.builder.GitBuilder;
import org.catools.athena.git.model.GitRepositoryDto;
import org.catools.athena.git.rest.controller.GitRepositoryController;
import org.catools.gatling.configs.SimulatorConfig;
import org.catools.gatling.population.common.GatlingRequestUtils;
import org.catools.gatling.population.common.PopulationInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.details;
import static io.gatling.javaapi.core.CoreDsl.group;
import static io.gatling.javaapi.core.CoreDsl.nothingFor;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class GitRepositoryPopulation {

  public static final List<String> repositoryStorage = Collections.synchronizedList(new ArrayList<>());

  public static List<PopulationInfo> getPopulationsInfo() {
    return List.of(
        getCreateGitRepositoryPopulation(),
        getSearchGitRepositoryPopulation()
    );
  }

  @NotNull
  private static PopulationInfo getCreateGitRepositoryPopulation() {
    return new PopulationInfo(
        scenario("Create GitRepository")
            .exec(group("GitRepository").on(createRandomGitRepository())).injectOpen(
                constantUsersPerSec(1).during(120)
            ),
        List.of(
            details("GitRepository", "Save GitRepository").failedRequests().count().is(0L),
            details("GitRepository", "Save GitRepository").responseTime().mean().lte(30),
            details("GitRepository", "Save GitRepository").responseTime().stdDev().lte(40),
            details("GitRepository", "Save GitRepository").responseTime().percentile3().lte(40),
            details("GitRepository", "Save GitRepository").responseTime().max().lte(500)
        )
    );
  }

  @NotNull
  private static PopulationInfo getSearchGitRepositoryPopulation() {
    return new PopulationInfo(
        scenario("Search GitRepository").exec(group("GitRepository").on(searchGitRepositoryByCode())).injectOpen(
            nothingFor(5),
            rampUsersPerSec(5).to(20).during(10),
            constantUsersPerSec(20).during(90),
            nothingFor(10)
        ),
        List.of(
            details("GitRepository", "Search GitRepository").failedRequests().count().is(0L),
            details("GitRepository", "Search GitRepository").responseTime().mean().lte(10),
            details("GitRepository", "Search GitRepository").responseTime().stdDev().lte(5),
            details("GitRepository", "Search GitRepository").responseTime().percentile3().lte(15),
            details("GitRepository", "Search GitRepository").responseTime().max().lte(100))
    );
  }

  private static HttpRequestActionBuilder createRandomGitRepository() {
    Function<Session, String> buildGitRepository = session -> JacksonUtil.toJsonString(GitBuilder.buildGitRepositoryDto());

    HttpRequestActionBuilder actionBuilder = http("Save GitRepository")
        .post(SimulatorConfig.getApiHost() + GitRepositoryController.REPOSITORY)
        .body(StringBody(buildGitRepository))
        .transformResponse((response, session) -> {
          GitRepositoryDto proj = JacksonUtil.toT(((StringRequestBody) response.request().getBody()).getContent(), GitRepositoryDto.class);
          repositoryStorage.add(proj.getName());
          repositoryStorage.add(proj.getUrl());
          return response;
        });

    return GatlingRequestUtils.decorateJsonPostRequest(201, actionBuilder);
  }

  private static HttpRequestActionBuilder searchGitRepositoryByCode() {

    HttpRequestActionBuilder actionBuilder = http("Search GitRepository")
        .get(SimulatorConfig.getApiHost() + GitRepositoryController.REPOSITORY)
        .queryParam("keyword", session -> repositoryStorage.stream().findAny().orElseThrow());

    return GatlingRequestUtils.decorateGetRequest(List.of(200), actionBuilder);
  }
}