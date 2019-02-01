package com.alamobot.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilmWithSeatCount {
    private Integer sessionId;
    private String filmName;
    private LocalDateTime sessionDateTime;
    private Integer seatCount;
    private String cinema;

    public void incrementSeatCount() {
        if(seatCount == null) {
            seatCount = 0;
        }
        seatCount++;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof FilmWithSeatCount) {
            return ((FilmWithSeatCount) o).getSessionId() == sessionId;
        }
        return false;
    }
}
