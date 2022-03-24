package com.turboparser.turbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TurboApplication {

    public static void main(String[] args) {
        SpringApplication.run(TurboApplication.class, args);
    }

//    public static void main(String[] args) {
//        System.out.println( "20 000 $".replaceAll("[ AZN$€]", ""));
//    }
}
