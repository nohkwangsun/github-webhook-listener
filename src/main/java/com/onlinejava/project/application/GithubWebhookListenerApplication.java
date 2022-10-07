package com.onlinejava.project.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.GenericApplicationContext;

@SpringBootApplication
@ComponentScan(basePackages = {"com.onlinejava.project"})
public class GithubWebhookListenerApplication {
	public static void main(String[] args) {
		SpringApplication.run(GithubWebhookListenerApplication.class, args);

	}

}
