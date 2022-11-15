package com.alamobot.config;

import com.alamobot.client.AlamoMovieClient;
import com.alamobot.services.AlertService;
import com.alamobot.services.CinemaService;
import com.alamobot.services.CleanupService;
import com.alamobot.services.FilmAlertEntityApiMapper;
import com.alamobot.services.FilmService;
import com.alamobot.services.HighWatermarkService;
import com.alamobot.services.MarketService;
import com.alamobot.services.MovieEntityMapper;
import com.alamobot.services.MovieService;
import com.alamobot.services.PaymentService;
import com.alamobot.services.QueueService;
import com.alamobot.services.SeatService;
import com.alamobot.services.mock.MockPaymentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    AlertService alertService() {
        return new AlertService();
    }

    @Bean
    QueueService queueService() {
        return new QueueService();
    }

    @Bean
    HighWatermarkService highWatermarkService() {
        return new HighWatermarkService();
    }

    @Bean
    MovieEntityMapper movieEntityMapper() {
        return new MovieEntityMapper();
    }

    @Bean
    AlamoMovieClient alamoMovieClient() {
        return new AlamoMovieClient();
    }

    @Bean
    RestTemplate simpleRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnProperty(value="payments.stub", havingValue = "false", matchIfMissing = true)
    PaymentService paymentService() {
        return new PaymentService();
    }

    @Bean
    CleanupService cleanupService() {
        return new CleanupService();
    }

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    FilmAlertEntityApiMapper filmAlertEntityApiMapper() {
        return new FilmAlertEntityApiMapper();
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    }


    @Bean
    @ConditionalOnProperty(value="payments.stub")
    PaymentService mockPaymentService() {
        MockPaymentService mockPaymentService = new MockPaymentService();
        mockPaymentService.setSeatBuyReturnStatus(true);
        return mockPaymentService;
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
