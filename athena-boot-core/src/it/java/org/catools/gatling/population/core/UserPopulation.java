package org.catools.gatling.population.core;

import com.google.gson.Gson;
import io.gatling.http.client.body.string.StringRequestBody;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import org.catools.athena.core.builder.CoreBuilder;
import org.catools.athena.core.model.UserDto;
import org.catools.athena.core.rest.controller.UserController;
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
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class UserPopulation {

  private static final List<String> usersStorage = Collections.synchronizedList(new ArrayList<>());

  public static List<PopulationInfo> getPopulationsInfo(int maxDuration) {
    return List.of(
        getCreateUserPopulation(maxDuration),
        getSearchUserPopulation(maxDuration)
    );
  }

  @NotNull
  private static PopulationInfo getCreateUserPopulation(int maxDuration) {
    return new PopulationInfo(
        scenario("Create User").exec(group("User").on(createRandomUser())).injectOpen(
            rampUsers(3).during(5),
            constantUsersPerSec(3).during(maxDuration)
        ),
        List.of(
            details("User", "Save User").failedRequests().count().is(0L),
            details("User", "Save User").responseTime().mean().lte(100),
            details("User", "Save User").responseTime().stdDev().lte(50),
            details("User", "Save User").responseTime().percentile3().lte(60),
            details("User", "Save User").responseTime().max().lte(1000))
    );
  }

  @NotNull
  private static PopulationInfo getSearchUserPopulation(int maxDuration) {
    int quiteTime = 5;
    int rampUpTime = Double.valueOf(maxDuration * 0.2).intValue();
    int executionTime = maxDuration - rampUpTime - quiteTime - quiteTime;
    return new PopulationInfo(
        scenario("Search User").exec(group("User").on(searchUserByCode())).injectOpen(
            nothingFor(quiteTime),
            rampUsersPerSec(5).to(50).during(rampUpTime),
            constantUsersPerSec(50).during(executionTime),
            nothingFor(quiteTime)
        ),
        List.of(
            details("User", "Search User").failedRequests().count().is(0L),
            details("User", "Search User").responseTime().mean().lte(10),
            details("User", "Search User").responseTime().stdDev().lte(5),
            details("User", "Search User").responseTime().percentile3().lte(15),
            details("User", "Search User").responseTime().max().lte(150))
    );
  }

  private static HttpRequestActionBuilder createRandomUser() {
    Function<Session, String> buildUser = session -> new Gson().toJson(CoreBuilder.buildUserDto());
    HttpRequestActionBuilder actionBuilder = http("Save User")
        .post(SimulatorConfig.getApiHost() + UserController.USER)
        .body(StringBody(buildUser))
        .transformResponse((response, session) -> {
          UserDto user = new Gson().fromJson(((StringRequestBody) response.request().getBody()).getContent(), UserDto.class);
          usersStorage.add(user.getUsername());
          user.getAliases().forEach(a -> usersStorage.add(a.getAlias()));
          return response;
        });

    return GatlingRequestUtils.decorateJsonPostRequest(201, actionBuilder);
  }

  private static HttpRequestActionBuilder searchUserByCode() {
    HttpRequestActionBuilder actionBuilder = http("Search User")
        .get(SimulatorConfig.getApiHost() + UserController.USER)
        .queryParam("keyword", session -> usersStorage.stream().findAny().orElseThrow());

    return GatlingRequestUtils.decorateGetRequest(List.of(200), actionBuilder);
  }
}