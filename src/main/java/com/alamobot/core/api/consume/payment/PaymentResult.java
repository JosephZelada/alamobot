package com.alamobot.core.api.consume.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResult {
    private String cinemaId;
    private int historyId;
    private String vistaBookingId;
    private int vistaBookingNumber;
    private int vistaTransactionNumber;
}
