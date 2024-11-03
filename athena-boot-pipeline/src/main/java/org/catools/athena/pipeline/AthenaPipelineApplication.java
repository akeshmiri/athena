package org.catools.athena.pipeline;

import org.catools.athena.common.feign.FeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = "org.catools.athena")
@Import(FeignConfiguration.class)
public class AthenaPipelineApplication {

  public static void main(String[] args) {
    SpringApplication.run(AthenaPipelineApplication.class, args);
  }

}
