package devlava.nudgeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NudgeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NudgeApiApplication.class, args);
    }

}
