package org.catools.athena.metric;

import org.catools.athena.common.feign.FeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = "org.catools.athena")
@Import(FeignConfiguration.class)
public class AthenaMetricApplication {

  public static void main(String[] args) {
    SpringApplication.run(AthenaMetricApplication.class, args);
  }

}
