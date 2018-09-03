package com.alamobot.core.api.consume.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDataRequestContainer {
    private String customerEmail;
    private String customerName;
    private Boolean joinVictory;
    private List<PaymentDataRequest> payments;
    private String userSessionId;
}
