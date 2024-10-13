package org.catools.gatling.population.core;

import com.google.gson.Gson;
import io.gatling.http.client.body.string.StringRequestBody;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import org.catools.athena.configs.StagedTestData;
import org.catools.athena.core.builder.CoreBuilder;
import org.catools.athena.core.model.EnvironmentDto;
import org.catools.athena.core.rest.controller.EnvironmentController;
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

public class EnvironmentPopulation {

  private static final List<String> environmentsStorage = Collections.synchronizedList(new ArrayList<>());

  public static List<PopulationInfo> getPopulationsInfo(int maxDuration) {
    return List.of(
        getCreateEnvironmentPopulation(maxDuration),
        getSearchEnvironmentPopulation(maxDuration)
    );
  }

  @NotNull
  private static PopulationInfo getCreateEnvironmentPopulation(int maxDuration) {
    return new PopulationInfo(
        scenario("Create Environment").exec(group("Environment").on(createRandomEnvironment())).injectOpen(
            constantUsersPerSec(2).during(maxDuration)
        ),
        List.of(
            details("Environment", "Save Environment").failedRequests().count().is(0L),
            details("Environment", "Save Environment").responseTime().mean().lte(30),
            details("Environment", "Save Environment").responseTime().stdDev().lte(50),
            details("Environment", "Save Environment").responseTime().percentile3().lte(40),
            details("Environment", "Save Environment").responseTime().max().lte(1000))
    );
  }

  @NotNull
  private static PopulationInfo getSearchEnvironmentPopulation(int maxDuration) {
    int quiteTime = 5;
    int rampUpTime = Double.valueOf(maxDuration * 0.2).intValue();
    int executionTime = maxDuration - rampUpTime - quiteTime - quiteTime;
    return new PopulationInfo(
        scenario("Search Environment").exec(group("Environment").on(searchEnvironmentByCode())).injectOpen(
            nothingFor(quiteTime),
            rampUsersPerSec(5).to(50).during(rampUpTime),
            constantUsersPerSec(50).during(executionTime),
            nothingFor(quiteTime)
        ),
        List.of(
            details("Environment", "Search Environment").failedRequests().count().is(0L),
            details("Environment", "Search Environment").responseTime().mean().lte(10),
            details("Environment", "Search Environment").responseTime().stdDev().lte(5),
            details("Environment", "Search Environment").responseTime().percentile3().lte(15),
            details("Environment", "Search Environment").responseTime().max().lte(150))
    );
  }

  private static HttpRequestActionBuilder createRandomEnvironment() {
    Function<Session, String> buildEnvironment = session -> new Gson().toJson(CoreBuilder.buildEnvironmentDto(StagedTestData.getProject(1).getCode()));
    HttpRequestActionBuilder actionBuilder = http("Save Environment")
        .post(SimulatorConfig.getApiHost() + EnvironmentController.ENVIRONMENT)
        .body(StringBody(buildEnvironment))
        .transformResponse((response, session) -> {
          EnvironmentDto environment = new Gson().fromJson(((StringRequestBody) response.request().getBody()).getContent(), EnvironmentDto.class);
          environmentsStorage.add(environment.getName());
          environmentsStorage.add(environment.getCode());
          return response;
        });

    return GatlingRequestUtils.decorateJsonPostRequest(201, actionBuilder);
  }

  private static HttpRequestActionBuilder searchEnvironmentByCode() {
    HttpRequestActionBuilder actionBuilder = http("Search Environment")
        .get(SimulatorConfig.getApiHost() + EnvironmentController.ENVIRONMENT)
        .queryParam("keyword", session -> environmentsStorage.stream().findAny().orElseThrow());

    return GatlingRequestUtils.decorateGetRequest(List.of(200), actionBuilder);
  }
}