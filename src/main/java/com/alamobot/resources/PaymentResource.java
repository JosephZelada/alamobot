package com.alamobot.resources;

import com.alamobot.core.ResourcePaths;
import com.alamobot.core.api.Seat;
import com.alamobot.services.PaymentService;
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

    @CrossOrigin
    @PostMapping("/{session_id}")
    public Boolean getSeatsForSessionId(@PathVariable("session_id") int sessionId, @RequestBody(required=false)ArrayList<Seat> seatsToBuy) {
        return paymentService.buySeats(sessionId, seatsToBuy);
    }
}
