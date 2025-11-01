package org.example.timetoplay;

import org.springframework.boot.SpringApplication;

public class TestTimetoplayApplication {

    public static void main(String[] args) {
        SpringApplication.from(TimetoplayApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
