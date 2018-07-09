package com.alamobot.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SeatEntity {
    @EmbeddedId
    private SeatEntityId seatEntityId;
    private String name;
    private int rowNumber;
    private int seatId;
    private int seatNumber;
    private int areaIndex;
    private int areaId;
    private int vistaAreaNumber;
    private int vistaRowIndex;
    private int vistaColumnIndex;
    private int priority;
    private int defaultPriceInCents;
    private int screenNumber;
    private String seatStyle;
    private String seatDescription;
    private String seatStatus;
    private String tableStyle;
    private String warningMessage;
    private int warningCode;
}
