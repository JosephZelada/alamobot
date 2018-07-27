package com.alamobot.core.api.consume.seats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Seat {
    private int id;
    private int seatNumber;
    private int areaIndex;
    private int rowIndex;
    private int columnIndex;
    private int areaId;
    private int vistaAreaNumber;
    private int vistaRowIndex;
    private int vistaColumnIndex;
    private int priority;
    private int defaultPriceInCents;
    private String seatStyle;
    private String seatDescription;
    private String seatStatus;
    private String tableStyle;
    private List<Warning> warnings;

//    "id": "20",
//            "seatNumber": "20",
//            "areaIndex": 0,
//            "rowIndex": 0,
//            "columnIndex": 1,
//            "areaId": 5,
//            "vistaAreaNumber": 5,
//            "vistaRowIndex": 8,
//            "vistaColumnIndex": 20,
//            "priority": 6,
//            "defaultPriceInCents": 1200,
//            "seatStyle": "NORMAL",
//            "seatDescription": "AVAILABLE",
//            "seatStatus": "EMPTY",
//            "tableStyle": "LONG_LEFT",
//            "warnings": []
}
