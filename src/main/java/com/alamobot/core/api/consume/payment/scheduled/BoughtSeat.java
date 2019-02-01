package com.alamobot.core.api.consume.payment.scheduled;

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
public class BoughtSeat {
    private Integer rowNumber;
    private Integer seatNumber;
}