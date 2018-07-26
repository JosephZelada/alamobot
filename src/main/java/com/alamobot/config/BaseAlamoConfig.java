package com.alamobot.config;

import com.alamobot.services.CinemaService;
import com.alamobot.services.CleanupService;
import com.alamobot.services.FilmService;
import com.alamobot.services.MarketService;
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

    @Bean
    MarketService marketService() {
        return new MarketService();
    }

    @Bean
    FilmService filmService() {
        return new FilmService();
    }

    @Bean
    CinemaService cinemaService() {
        return new CinemaService();
    }

    @Bean
    CleanupService cleanupService() {
        return new CleanupService();
    }
}
