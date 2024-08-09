package com.assignment.BankingApp;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankingAppApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankingAppApplication.class);

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        String dbPassword = dotenv.get("DB_PASSWORD");
        String dbTestPassword = dotenv.get("DB_TEST_PASSWORD");

        if (dbPassword != null) {
            System.setProperty("DB_PASSWORD", dbPassword);
        } else {
            LOGGER.warn("DB_PASSWORD not found in .env file.");
        }

        if (dbTestPassword != null) {
            System.setProperty("DB_TEST_PASSWORD", dbTestPassword);
        } else {
            LOGGER.warn("DB_TEST_PASSWORD not found in .env file.");
        }

        SpringApplication.run(BankingAppApplication.class, args);
    }
}
