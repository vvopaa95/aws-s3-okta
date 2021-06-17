package com.example.awsbased;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan(value = { "com.example.awsbased.aws", "com.example.awsbased.home" })
@SpringBootApplication
public class AwsBasedApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwsBasedApplication.class, args);
    }

}
