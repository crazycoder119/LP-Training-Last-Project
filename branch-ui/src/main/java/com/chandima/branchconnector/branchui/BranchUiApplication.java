package com.chandima.branchconnector.branchui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class BranchUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BranchUiApplication.class, args);
	}

}
