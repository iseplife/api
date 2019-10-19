package com.iseplife.api.conf;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@Configuration
@EnableSwagger2
@Profile("default")
@ComponentScan("com.iseplife.api")
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
      .select()
      .apis(RequestHandlerSelectors.any())
      .paths(PathSelectors.any())
      .build()
      .select()
      .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
      .build()
      .globalOperationParameters(
        Arrays.asList(
          new ParameterBuilder()
          .name("Authorization")
          .description("Auth header")
          .modelRef(new ModelRef("string"))
          .parameterType("header")
          .defaultValue("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6W10sImlzcyI6IklzZXBMaXZlIiwiaWQiOjEsImV4cCI6MTU1NDcxODk2NSwiaWF0IjoxNTAyODc4OTY1fQ.p7KdAF_f5mCIfcyzQ7hW5_pEfdwfbIoH9eRLit4D_AM")
          .required(true)
          .build(),
        new ParameterBuilder()
          .name("X-Refresh-Token")
          .description("Refresh header")
          .modelRef(new ModelRef("string"))
          .parameterType("header")
          .defaultValue("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6W10sImlzcyI6IklzZXBMaXZlIiwiaWQiOjEsImV4cCI6MTU1NDcxODk2NSwiaWF0IjoxNTAyODc4OTY1fQ.p7KdAF_f5mCIfcyzQ7hW5_pEfdwfbIoH9eRLit4D_AM")
          .required(true)
          .build()
        ));
  }

}
