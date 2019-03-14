package com.alamobot.services.mock;

import com.alamobot.core.api.Seat;
import com.alamobot.services.PaymentService;
import com.alamobot.services.SeatService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Data
public class MockPaymentService extends PaymentService {

    @Autowired
    SeatService seatService;

    private boolean seatBuyReturnStatus;

    @Override
    public boolean buySeats(int sessionId, List<Seat> seatsToBuy) {
        seatService.markSeatsAsBought(seatsToBuy);
        return seatBuyReturnStatus;
    }
}
