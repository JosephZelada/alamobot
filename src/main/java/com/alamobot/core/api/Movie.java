package com.alamobot.core.api;

import com.alamobot.core.domain.CinemaEntity;
import com.alamobot.core.domain.FilmEntity;
import com.alamobot.core.domain.FormatEntity;
import com.alamobot.core.domain.MarketEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(of = "sessionId")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    private int sessionId;
    private String sessionDateTime;
    private String sessionStatus;
    private MarketEntity market;
    private CinemaEntity cinema;
    private FilmEntity film;
    private FormatEntity format;
    private int seatsLeft;
}