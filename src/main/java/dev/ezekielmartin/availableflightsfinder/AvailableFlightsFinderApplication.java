package dev.ezekielmartin.availableflightsfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AvailableFlightsFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvailableFlightsFinderApplication.class, args);
    }

}
