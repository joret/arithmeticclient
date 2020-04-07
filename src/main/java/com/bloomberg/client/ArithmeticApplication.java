package com.bloomberg.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ArithmeticApplication {

	//TODO create log definition file
	public static void main(String[] args) {
		SpringApplication.run(ArithmeticApplication.class, args);
	}
}
