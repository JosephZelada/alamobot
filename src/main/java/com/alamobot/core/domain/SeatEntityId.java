package com.alamobot.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SeatEntityId implements Serializable {
    private Integer sessionId;
    private Integer rowIndex;
    private Integer columnIndex;
}
