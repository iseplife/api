package com.iseplife.api;

import java.lang.reflect.Field;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.github.lambdaexpression.util.SpringBeanUtils;

@SpringBootApplication
@EnableCaching
public class IseplifeApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext ac = SpringApplication.run(IseplifeApplication.class, args);

    DatabaseSeeder tdbs = ac.getBeanFactory().createBean(DatabaseSeeder.class);
    tdbs.seedDatabase();
    
    //Fix for weird bug of request-body-param
    try {
      Field f = SpringBeanUtils.class.getDeclaredField("ctx");
      f.setAccessible(true);
      f.set(null, ac);
    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
      e.printStackTrace();
    }
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
}
