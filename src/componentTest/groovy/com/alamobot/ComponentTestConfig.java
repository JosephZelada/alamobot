package com.alamobot;

import com.alamobot.client.AlamoMovieClient;
import com.alamobot.client.mock.MockAlamoMovieClient;
import com.alamobot.services.PaymentService;
import com.alamobot.services.mock.MockPaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import({
        AlamoBot.class
})
public class ComponentTestConfig {

    @Bean
    @Primary
    PaymentService paymentService() {
        MockPaymentService mockPaymentService = new MockPaymentService();
        mockPaymentService.setSeatBuyReturnStatus(true);
        return mockPaymentService;
    }

    @Bean
    @Primary
    AlamoMovieClient alamoMovieClient() {
        return new MockAlamoMovieClient();
    }
}
