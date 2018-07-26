package com.alamobot.core.api.showtime;

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
public class Cinema {
    private String CinemaId;
    private String CinemaName;
    private String CinemaSlug;
    private List<Film> Films;
}
