package org.example.catalogclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = FilmClient.class)
public class CatalogClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatalogClientApplication.class, args);
    }
}
