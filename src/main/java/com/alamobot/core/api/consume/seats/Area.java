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
public class Area {
    private String areaCategoryCode;
    private String description;
    private int columnCount;
    private int rowCount;
    private int numberOfSeats;
    private List<Row> rows;
}
