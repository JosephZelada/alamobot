package com.alamobot.core.api.market;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    private int SessionId;
    private String SessionStatus;
    private String SessionDateTime;
    private String SessionType;
    private int SeatsLeft;
}
