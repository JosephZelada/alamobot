package com.alamobot.core.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatMap {
    private String filmName;
    private Integer theaterNum;
    private Map<Integer, Map<Integer, Seat>> seats;
}
