package org.fusif.game_detector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GameDetectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameDetectorApplication.class, args);
    }

}
