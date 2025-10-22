package com.buckb.spring.academy.cashcard;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CashCardApplication {

	public static void main(String[] args) {
		Dotenv.configure()
				.ignoreIfMalformed()
				.systemProperties()
				.load();

		SpringApplication.run(CashCardApplication.class, args);
	}

}
