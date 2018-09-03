package com.alamobot.core.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatEssentials {
    private int rowIndex;
    private int columnIndex;
    private int areaIndex;
}

