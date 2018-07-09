package com.alamobot.core.domain;


import com.alamobot.core.persistence.LocalDateTimeAttributeConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(of = "sessionId")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieEntity {
    @Id
    private int sessionId;
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime sessionDateTime;
    private String sessionStatus;
    private String marketId;
    private String cinemaId;
    private String filmId;
    private String formatId;
    private int seatsLeft;
    private Boolean watched;
}