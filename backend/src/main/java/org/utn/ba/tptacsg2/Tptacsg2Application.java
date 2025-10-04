package org.utn.ba.tptacsg2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "org.utn.ba.tptacsg2.repositories.db")
public class Tptacsg2Application {

	public static void main(String[] args) {
		SpringApplication.run(Tptacsg2Application.class, args);
	}

}
