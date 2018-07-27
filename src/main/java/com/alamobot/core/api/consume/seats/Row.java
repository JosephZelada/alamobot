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
public class Row {
    private int areaIndex;
    private int rowIndex;
    private String name;
    private int rowNumber;
    private boolean isEmpty;
    private List<Seat> seats;
}
