package com.alamobot.core.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilmShowtime {
    private Integer sessionId;
    private LocalDateTime showtime;
    private Boolean watched;
}
