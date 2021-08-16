package com.hubpan.IslandBooking;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

@SpringBootApplication
public class IslandBookingApplication {

    protected final static Log logger = LogFactory.getLog(IslandBookingApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(IslandBookingApplication.class, args);
        logger.info(System.getProperty("user.timezone"));
    }

}
