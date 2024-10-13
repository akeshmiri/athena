package org.catools.gatling.population.core;

import io.gatling.http.client.body.string.StringRequestBody;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import org.catools.athena.apispec.model.ApiSpecDto;
import org.catools.athena.common.utils.JacksonUtil;
import org.catools.athena.configs.StagedTestData;
import org.catools.athena.spec.builder.ApiSpecBuilder;
import org.catools.athena.spec.rest.controller.ApiSpecController;
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

public class SpecPopulation {

  private static final List<String> SPEC_STORAGE = Collections.synchronizedList(new ArrayList<>());
  private static final String PROJECT_CODE = StagedTestData.getProject(1).getCode();

  public static List<PopulationInfo> getPopulationsInfo(int maxDuration) {
    return List.of(
        getCreateSpecPopulation(maxDuration),
        getSearchSpecPopulation(maxDuration)
    );
  }

  @NotNull
  private static PopulationInfo getCreateSpecPopulation(int maxDuration) {
    return new PopulationInfo(
        scenario("Create Api Spec").exec(group("Api Spec").on(createRandomSpec())).injectOpen(
            constantUsersPerSec(2).during(maxDuration)
        ),
        List.of(
            details("Api Spec", "Save Api Spec").failedRequests().count().is(0L),
            details("Api Spec", "Save Api Spec").responseTime().mean().lte(350),
            details("Api Spec", "Save Api Spec").responseTime().stdDev().lte(300),
            details("Api Spec", "Save Api Spec").responseTime().percentile3().lte(800),
            details("Api Spec", "Save Api Spec").responseTime().max().lte(3000))
    );
  }

  @NotNull
  private static PopulationInfo getSearchSpecPopulation(int maxDuration) {
    int quiteTime = 10;
    int rampUpTime = Double.valueOf(maxDuration * 0.2).intValue();
    int executionTime = maxDuration - rampUpTime - quiteTime - quiteTime;
    return new PopulationInfo(
        scenario("Search Api Spec").exec(group("Api Spec").on(searchSpecByCode())).injectOpen(
            nothingFor(quiteTime),
            rampUsersPerSec(5).to(50).during(rampUpTime),
            constantUsersPerSec(50).during(executionTime),
            nothingFor(quiteTime)
        ),
        List.of(
            details("Api Spec", "Search Api Spec").failedRequests().count().is(0L),
            details("Api Spec", "Search Api Spec").responseTime().mean().lte(100),
            details("Api Spec", "Search Api Spec").responseTime().stdDev().lte(20),
            details("Api Spec", "Search Api Spec").responseTime().percentile3().lte(200),
            details("Api Spec", "Search Api Spec").responseTime().max().lte(1000))
    );
  }

  private static HttpRequestActionBuilder createRandomSpec() {
    Function<Session, String> buildSpec = session -> JacksonUtil.toJsonString(ApiSpecBuilder.buildApiSpecDto(PROJECT_CODE));
    HttpRequestActionBuilder actionBuilder = http("Save Api Spec")
        .post(SimulatorConfig.getApiHost() + ApiSpecController.API_SPEC)
        .body(StringBody(buildSpec))
        .transformResponse((response, session) -> {
          ApiSpecDto spec = JacksonUtil.toT(((StringRequestBody) response.request().getBody()).getContent(), ApiSpecDto.class);
          SPEC_STORAGE.add(spec.getName());
          return response;
        });

    return GatlingRequestUtils.decorateJsonPostRequest(201, actionBuilder);
  }

  private static HttpRequestActionBuilder searchSpecByCode() {
    HttpRequestActionBuilder actionBuilder = http("Search Api Spec")
        .get(SimulatorConfig.getApiHost() + ApiSpecController.API_SPEC)
        .queryParam("projectCode", PROJECT_CODE)
        .queryParam("name", session -> SPEC_STORAGE.stream().findAny().orElseThrow());

    return GatlingRequestUtils.decorateGetRequest(List.of(200), actionBuilder);
  }
}