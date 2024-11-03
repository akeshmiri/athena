package org.catools.athena.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FeignBuilder {
  public <T> T feignBuilder(Class<T> apiType, ObjectMapper objectMapper, String host, Integer port) {
    return feignBuilder(apiType, objectMapper)
        .target(apiType, String.format("http://%s:%s", host, port));
  }

  public <T> Feign.Builder feignBuilder(Class<T> apiType, ObjectMapper objectMapper) {
    return Feign
        .builder()
        .encoder(new JacksonEncoder(objectMapper))
        .decoder(new JacksonDecoder(objectMapper))
        .retryer(Retryer.NEVER_RETRY)
        .logger(new Slf4jLogger(apiType));
  }
}
