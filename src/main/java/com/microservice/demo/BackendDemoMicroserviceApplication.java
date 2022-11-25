package com.microservice.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class BackendDemoMicroserviceApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(BackendDemoMicroserviceApplication.class, args);
	}

}
