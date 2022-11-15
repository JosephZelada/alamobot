package com.alamobot.services.mock;

import com.alamobot.core.api.Seat;
import com.alamobot.services.PaymentService;
import com.alamobot.services.QueueAuthorization;
import com.alamobot.services.SeatService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Data
public class MockPaymentService extends PaymentService {

    @Autowired
    SeatService seatService;

    private boolean seatBuyReturnStatus;

    private QueueAuthorization queueAuthorization;

    private String marketSlug;

    @Override
    public boolean buySeats(int sessionId, List<Seat> seatsToBuy, QueueAuthorization queueAuthorization, String marketSlug) {
        seatService.markSeatsAsBought(seatsToBuy);
        this.queueAuthorization = queueAuthorization;
        this.marketSlug = marketSlug;
        return seatBuyReturnStatus;
    }
}
