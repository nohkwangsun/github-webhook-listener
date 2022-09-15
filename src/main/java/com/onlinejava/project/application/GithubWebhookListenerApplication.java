package com.onlinejava.project.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.onlinejava.project"})
public class GithubWebhookListenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubWebhookListenerApplication.class, args);
	}

}
