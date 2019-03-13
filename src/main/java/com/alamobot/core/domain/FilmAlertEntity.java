package com.alamobot.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FilmAlertEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private String filmName;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> preferredCinemas;
    private LocalTime earliestShowtime;
    private LocalTime latestShowtime;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<DayOfWeek> preferredDaysOfTheWeek;
    private Boolean overrideSeatingAlgorithm;
    private Integer seatCount;
}
