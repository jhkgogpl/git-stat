package com.asiainfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Jacky on 2015/7/7.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

    public static void main( String[] args ) {
        SpringApplication.run(Application.class, args);
    }
}
