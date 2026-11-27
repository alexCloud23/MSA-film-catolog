package org.example.msa1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Msa1Application {

    public static void main(String[] args) {
        SpringApplication.run(Msa1Application.class, args);
    }

}
