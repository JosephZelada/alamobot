package com.alamobot.core.api.showtime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    private int SessionId;
    private String SessionStatus;
    private LocalDateTime SessionDateTime;
    private String SessionType;
    private int SeatsLeft;
}
