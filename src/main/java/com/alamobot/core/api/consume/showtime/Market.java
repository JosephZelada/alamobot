package com.alamobot.core.api.consume.showtime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Market {
    private String MarketId;
    private String MarketName;
    private String MarketSlug;
    private List<FilmDate> Dates;
}
