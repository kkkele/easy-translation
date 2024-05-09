package com.superkele.demo;


import com.superkele.translation.boot.annotation.EnableTranslation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableTranslation("com.czy")
public class EasyTranslationApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyTranslationApplication.class, args);
    }
}
