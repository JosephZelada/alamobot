package com.alamobot.core.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Seat {
    private int id;
    private int rowIndex;
    private int columnIndex;
    private int rowNumber;
    private int seatNumber;
    private int areaIndex;
    private String seatStatus;
    private boolean seatBought;
    private String personInSeat;
}
