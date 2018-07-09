package com.alamobot.core.api.seats;

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
public class Warning {
    private int category;
    private int code;
    private String description;
//    "category": 107,
//            "code": 101,
//            "description": "ADA Accessible Seating"
}
