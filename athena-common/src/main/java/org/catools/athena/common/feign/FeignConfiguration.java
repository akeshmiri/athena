package org.catools.athena.common.feign;

import feign.Contract;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableFeignClients(basePackages = "org.catools.athena")
@Profile({"prod", "dev"})
public class FeignConfiguration {
  @Bean
  @SuppressWarnings("unused")
  public Contract feignContract() {
    return new feign.Contract.Default();
  }

}