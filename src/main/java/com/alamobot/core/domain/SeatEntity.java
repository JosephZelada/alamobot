package com.alamobot.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SeatEntity {
    @Id
    private Integer id;
    private Integer sessionId;
    private Integer rowIndex;
    private Integer columnIndex;
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
    //can be EMPTY (Available), NONE (Not a seat), SOLD (Someone else bought it), TAKEN (I bought it, someone is sitting in it)
    private String seatStatus;
    private String tableStyle;
    private String warningMessage;
    private int warningCode;
    private boolean seatBought;
    private String personInSeat;
}
