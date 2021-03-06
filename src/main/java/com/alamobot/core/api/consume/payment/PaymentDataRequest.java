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
public class PaymentDataRequest {
    private boolean billFullOutstandingAmount;
    private int paymentValueCents;
    private boolean saveCardToCardWallet;
    private boolean useAsBookingRef;
    private String walletAccessToken;

}
