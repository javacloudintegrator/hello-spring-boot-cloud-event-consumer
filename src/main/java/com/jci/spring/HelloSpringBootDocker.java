package com.jci.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class HelloSpringBootDocker {

  public static void main(String[] args) {
    SpringApplication.run(HelloSpringBootDocker.class, args);
  }

  @RestController
  class HelloSpringBootDockerController {
    @GetMapping("/{name}")
    String hello(@PathVariable("name") String name) {
      return "Hello " + name + " from Spring Boot Docker JCI!";
    }
  }
}
