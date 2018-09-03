package com.alamobot.core.api.consume.payment;

import com.alamobot.core.api.consume.seats.Warning;
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
public class AlamoError {
    private String description;
    private Warning errorCode;
    private Warning detailCode;
}
