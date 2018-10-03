package com.alamobot.services.mock;

import com.alamobot.core.api.Seat;
import com.alamobot.services.PaymentService;
import lombok.Data;

import java.util.ArrayList;

@Data
public class MockPaymentService extends PaymentService {

    private boolean seatBuyReturnStatus;

    @Override
    public boolean buySeats(int sessionId, ArrayList<Seat> seatsToBuy) {
        return seatBuyReturnStatus;
    }
}
