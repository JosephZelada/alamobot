package com.alamobot.resources;

import com.alamobot.core.ResourcePaths;
import com.alamobot.core.api.Seat;
import com.alamobot.core.persistence.MarketRepository;
import com.alamobot.core.persistence.MovieRepository;
import com.alamobot.services.PaymentService;
import com.alamobot.services.QueueAuthorization;
import com.alamobot.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping(path = ResourcePaths.PAYMENT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentResource {

    @Autowired
    PaymentService paymentService;

    @Autowired
    SeatService seatService;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    MarketRepository marketRepository;

    @CrossOrigin
    @PostMapping("/{session_id}")
    public Boolean getSeatsForSessionId(@PathVariable("session_id") int sessionId, @RequestBody(required=false)ArrayList<Seat> seatsToBuy) {
        /*
        We pass an empty queue auth here because this endpoint is only hit by the alamobot frontend, which should
        never be used in situations where a queue is present. For said situations, rely solely on the alerts to buy
        seats
         */
        String marketSlug =
                marketRepository.findById(movieRepository.findBySessionId(sessionId).getMarketId()).get().getSlug();
        return paymentService.buySeats(sessionId, seatsToBuy, new QueueAuthorization(), marketSlug);
    }
}
