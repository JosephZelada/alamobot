package com.alamobot.core.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilmAlert {
    private Integer id;
    private String filmName;
    private Set<String> preferredCinemas;
    private LocalDateTime earliestShowtime;
    private LocalDateTime latestShowtime;
    private Set<String> preferredDaysOfTheWeek;
    private Boolean overrideSeatingAlgorithm;
    private Integer seatCount;
}
