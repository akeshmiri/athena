package org.catools.gatling.population.core;

import com.google.gson.Gson;
import io.gatling.http.client.body.string.StringRequestBody;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import org.catools.athena.core.builder.CoreBuilder;
import org.catools.athena.core.model.ProjectDto;
import org.catools.athena.core.rest.controller.ProjectController;
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

public class ProjectPopulation {

  private static final List<String> projectStorage = Collections.synchronizedList(new ArrayList<>());

  public static List<PopulationInfo> getPopulationsInfo(int maxDuration) {
    return List.of(
        getCreateProjectPopulation(maxDuration),
        getSearchProjectPopulation(maxDuration)
    );
  }

  @NotNull
  private static PopulationInfo getCreateProjectPopulation(int maxDuration) {
    return new PopulationInfo(
        scenario("Create Project").exec(group("Project").on(createRandomProject())).injectOpen(
            constantUsersPerSec(2).during(maxDuration)
        ),
        List.of(
            details("Project", "Save Project").failedRequests().count().is(0L),
            details("Project", "Save Project").responseTime().mean().lte(30),
            details("Project", "Save Project").responseTime().stdDev().lte(60),
            details("Project", "Save Project").responseTime().percentile3().lte(40),
            details("Project", "Save Project").responseTime().max().lte(1000))
    );
  }

  @NotNull
  private static PopulationInfo getSearchProjectPopulation(int maxDuration) {
    int quiteTime = 5;
    int rampUpTime = Double.valueOf(maxDuration * 0.2).intValue();
    int executionTime = maxDuration - rampUpTime - quiteTime - quiteTime;
    return new PopulationInfo(
        scenario("Search Project").exec(group("Project").on(searchProjectByCode())).injectOpen(
            nothingFor(quiteTime),
            rampUsersPerSec(5).to(50).during(rampUpTime),
            constantUsersPerSec(50).during(executionTime),
            nothingFor(quiteTime)
        ),
        List.of(
            details("Project", "Search Project").failedRequests().count().is(0L),
            details("Project", "Search Project").responseTime().mean().lte(10),
            details("Project", "Search Project").responseTime().stdDev().lte(5),
            details("Project", "Search Project").responseTime().percentile3().lte(15),
            details("Project", "Search Project").responseTime().max().lte(150))
    );
  }

  private static HttpRequestActionBuilder createRandomProject() {
    Function<Session, String> buildProject = session -> new Gson().toJson(CoreBuilder.buildProjectDto());

    HttpRequestActionBuilder actionBuilder = http("Save Project")
        .post(SimulatorConfig.getApiHost() + ProjectController.PROJECT)
        .body(StringBody(buildProject))
        .transformResponse((response, session) -> {
          ProjectDto proj = new Gson().fromJson(((StringRequestBody) response.request().getBody()).getContent(), ProjectDto.class);
          projectStorage.add(proj.getName());
          projectStorage.add(proj.getCode());
          return response;
        });

    return GatlingRequestUtils.decorateJsonPostRequest(201, actionBuilder);
  }

  private static HttpRequestActionBuilder searchProjectByCode() {

    HttpRequestActionBuilder actionBuilder = http("Search Project")
        .get(SimulatorConfig.getApiHost() + ProjectController.PROJECT)
        .queryParam("keyword", session -> projectStorage.stream().findAny().orElseThrow());

    return GatlingRequestUtils.decorateGetRequest(List.of(200), actionBuilder);
  }
}