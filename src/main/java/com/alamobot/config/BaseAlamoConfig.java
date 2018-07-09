package com.alamobot.config;

import com.alamobot.services.MovieService;
import com.alamobot.services.SeatService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseAlamoConfig {

    @Bean
    MovieService movieService() {
        return new MovieService();
    }

    @Bean
    SeatService seatService() {
        return new SeatService();
    }
}
