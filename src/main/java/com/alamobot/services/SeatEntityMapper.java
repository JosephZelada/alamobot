package com.alamobot.services;


import com.alamobot.core.api.seats.Area;
import com.alamobot.core.api.seats.DataContainer;
import com.alamobot.core.api.seats.Row;
import com.alamobot.core.api.seats.Seat;
import com.alamobot.core.api.seats.Warning;
import com.alamobot.core.domain.SeatEntity;
import com.alamobot.core.domain.SeatEntityId;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SeatEntityMapper {

    Random random = new Random();

    //TODO: Streamify this, refactor and uncle bob this. Get it contained within one screen
    List<SeatEntity> dataToSeatEntity(DataContainer dataContainer, int sessionId) {
        ArrayList<SeatEntity> seatEntities = new ArrayList<>();
        for(Area area: dataContainer.getData().getSeatingData().getAreas()) {
            for(Row row: area.getRows()) {
                for(Seat seat: row.getSeats()) {
                    List<Warning> warningList = seat.getWarnings();
                    String warningMessage = "";
                    int warningCode = 0;
                    if(warningList != null && warningList.size() > 0) {
                        Warning warning = warningList.get(0);
                        warningMessage = warning.getDescription();
                        warningCode = warning.getCode();
                    }
                    seatEntities.add(
                            new SeatEntity(
                                    new SeatEntityId(
                                            sessionId,
                                            seat.getRowIndex(),
                                            seat.getColumnIndex()
                                    ),
                                    row.getName(),
                                    row.getRowNumber(),
                                    seat.getId(),
                                    seat.getSeatNumber(),
                                    seat.getAreaIndex(),
                                    seat.getAreaId(),
                                    seat.getVistaAreaNumber(),
                                    seat.getVistaRowIndex(),
                                    seat.getVistaColumnIndex(),
                                    seat.getPriority(),
                                    seat.getDefaultPriceInCents(),
                                    seat.getSeatNumber(),
                                    seat.getSeatStyle(),
                                    seat.getSeatDescription(),
                                    seat.getSeatStatus(),
                                    seat.getTableStyle(),
                                    warningMessage,
                                    warningCode
                            )
                    );
                }
            }
        }
        return seatEntities;
    }
}
