package com.iseplife.api;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class IseplifeApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext ac = SpringApplication.run(IseplifeApplication.class, args);
    DatabaseSeeder tdbs = ac.getBeanFactory().createBean(DatabaseSeeder.class);
    tdbs.seedDatabase();
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
}
