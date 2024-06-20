package com.superkele.demo;


import com.superkele.translation.boot.annotation.EnableTranslation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableTranslation
@SpringBootApplication
public class EasyTranslationDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyTranslationDemoApplication.class, args);
    }
}
