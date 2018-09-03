package com.alamobot.config;

import com.alamobot.services.CinemaService;
import com.alamobot.services.CleanupService;
import com.alamobot.services.FilmService;
import com.alamobot.services.MarketService;
import com.alamobot.services.MovieService;
import com.alamobot.services.PaymentService;
import com.alamobot.services.SeatService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;

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
    PaymentService paymentService() {
        return new PaymentService();
    }

    @Bean
    CleanupService cleanupService() {
        return new CleanupService();
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    }

    @Bean
    //Just to hack the servers to let them think I'm coming from Chrome
    HttpEntity<String> httpHeaderEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "chrome");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>("parameters", headers);
    }

    @Bean
    //Just to hack the servers to let them think I'm coming from Chrome
    HttpHeaders httpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        ArrayList<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.ALL);
        mediaTypes.add(MediaType.TEXT_PLAIN);
        headers.setAccept(mediaTypes);
        headers.setConnection("keep-alive");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "chrome");
        return headers;
    }
}
