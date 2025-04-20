package org.fusif.game_detector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class ApplicationDetector {

    public static void main(String[] args) {
        log.info("Starting application...");
        SpringApplication.run(ApplicationDetector.class, args);
    }

}
