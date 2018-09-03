package com.alamobot.core.api.consume.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@JsonIgnoreProperties
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String userSessionId;
    private LocalDateTime lastUpdatedUtc;
    private String cinemaId;
    private int sessionId;
    private int screenNumber;
    private int theaterNumber;
    private int totalValueCents;
    private int bookinFeeValueCents;
}
