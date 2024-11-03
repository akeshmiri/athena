package org.catools.athena.kube;

import org.catools.athena.common.feign.FeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = "org.catools.athena")
@Import(FeignConfiguration.class)
public class AthenaKubeApplication {

  public static void main(String[] args) {
    SpringApplication.run(AthenaKubeApplication.class, args);
  }

}
