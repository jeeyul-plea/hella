package kr.plea.hella;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HellaApplication {

    public static void main(String[] args) {
        SpringApplication.run(HellaApplication.class, args);
    }

}
