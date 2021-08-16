package com.hubpan.IslandBooking;

import com.hubpan.IslandBooking.core.model.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper(TimeZone.getDefault());
    }
}
