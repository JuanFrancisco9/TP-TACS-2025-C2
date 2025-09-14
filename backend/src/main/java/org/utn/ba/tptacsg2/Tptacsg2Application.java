package org.utn.ba.tptacsg2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
       // SecurityAutoConfiguration.class
})
public class Tptacsg2Application {

	public static void main(String[] args) {
		SpringApplication.run(Tptacsg2Application.class, args);
	}

}
