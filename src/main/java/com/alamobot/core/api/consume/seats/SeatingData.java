package com.alamobot.core.api.consume.seats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@lombok.Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatingData {
    private int screenNumber;
    private List<Area> areas;

}
