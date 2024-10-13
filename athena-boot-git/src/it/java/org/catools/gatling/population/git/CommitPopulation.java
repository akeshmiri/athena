package org.catools.gatling.population.git;

import io.gatling.http.client.body.string.StringRequestBody;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import org.catools.athena.common.utils.JacksonUtil;
import org.catools.athena.common.utils.RetryUtils;
import org.catools.athena.configs.StagedTestData;
import org.catools.athena.core.model.UserDto;
import org.catools.athena.git.builder.GitBuilder;
import org.catools.athena.git.model.CommitDto;
import org.catools.athena.git.rest.controller.CommitController;
import org.catools.gatling.configs.SimulatorConfig;
import org.catools.gatling.population.common.GatlingRequestUtils;
import org.catools.gatling.population.common.PopulationInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.details;
import static io.gatling.javaapi.core.CoreDsl.group;
import static io.gatling.javaapi.core.CoreDsl.nothingFor;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class CommitPopulation {

  private static final List<String> commitStorage = Collections.synchronizedList(new ArrayList<>());

  public static List<PopulationInfo> getPopulationsInfo() {
    return List.of(
        getCreateCommitPopulation(),
        getSearchCommitPopulation()
    );
  }

  @NotNull
  private static PopulationInfo getCreateCommitPopulation() {
    return new PopulationInfo(
        scenario("Create Commit").exec(group("Commit").on(createRandomCommit())).injectOpen(
            nothingFor(5),
            rampUsersPerSec(1).to(10).during(20),
            constantUsersPerSec(10).during(90),
            nothingFor(10)
        ),
        List.of(
            details("Commit", "Save Commit").failedRequests().count().is(0L),
            details("Commit", "Save Commit").responseTime().mean().lte(800),
            details("Commit", "Save Commit").responseTime().stdDev().lte(250),
            details("Commit", "Save Commit").responseTime().percentile3().lte(1500),
            details("Commit", "Save Commit").responseTime().max().lte(3000))
    );
  }

  @NotNull
  private static PopulationInfo getSearchCommitPopulation() {
    return new PopulationInfo(
        scenario("Search Commit").exec(group("Commit").on(searchCommitByCode())).injectOpen(
            nothingFor(10),
            atOnceUsers(1)
        ),
        List.of(
            details("Commit", "Search Commit").failedRequests().count().is(0L),
            details("Commit", "Search Commit").responseTime().mean().lte(200),
            details("Commit", "Search Commit").responseTime().stdDev().lte(5),
            details("Commit", "Search Commit").responseTime().percentile3().lte(200),
            details("Commit", "Search Commit").responseTime().max().lte(400))
    );
  }

  private static HttpRequestActionBuilder createRandomCommit() {
    UserDto user = new UserDto(StagedTestData.getUser(1).getUsername());
    Function<Session, String> buildCommit = session -> {
      String repository = RetryUtils.retry(5, 500, idx -> GitRepositoryPopulation.repositoryStorage.stream().findAny().orElseThrow());
      return JacksonUtil.toJsonString(GitBuilder.buildCommitDto(repository, user, user));
    };
    HttpRequestActionBuilder actionBuilder = http("Save Commit")
        .post(SimulatorConfig.getApiHost() + CommitController.COMMIT)
        .body(StringBody(buildCommit))
        .transformResponse((response, session) -> {
          CommitDto commit = JacksonUtil.toT(((StringRequestBody) response.request().getBody()).getContent(), CommitDto.class);
          commitStorage.add(commit.getHash());
          return response;
        });

    return GatlingRequestUtils.decorateJsonPostRequest(201, actionBuilder);
  }

  private static HttpRequestActionBuilder searchCommitByCode() {

    HttpRequestActionBuilder actionBuilder = http("Search Commit")
        .get(SimulatorConfig.getApiHost() + CommitController.COMMIT)
        .queryParam("hash", session -> commitStorage.stream().findAny().orElseThrow());

    return GatlingRequestUtils.decorateGetRequest(List.of(200), actionBuilder);
  }
}
