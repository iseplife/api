package com.iseplive.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableCaching
public class IsepliveApplication {
  public static void main(String[] args) {
    ConfigurableApplicationContext ac = SpringApplication.run(IsepliveApplication.class, args);
    DatabaseSeeder tdbs = ac.getBeanFactory().createBean(DatabaseSeeder.class);
    tdbs.seedDatabase();
  }
}
