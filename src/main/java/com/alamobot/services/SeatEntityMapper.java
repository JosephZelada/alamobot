package com.alamobot.services;


import com.alamobot.core.api.consume.seats.Area;
import com.alamobot.core.api.consume.seats.DataContainer;
import com.alamobot.core.api.consume.seats.Row;
import com.alamobot.core.api.consume.seats.Seat;
import com.alamobot.core.api.consume.seats.Warning;
import com.alamobot.core.domain.SeatEntity;

import java.util.ArrayList;
import java.util.List;

public class SeatEntityMapper {
    //TODO: Streamify this, refactor and uncle bob this. Get it contained within one screen
    List<SeatEntity> dataToSeatEntity(DataContainer dataContainer, int sessionId) {
        ArrayList<SeatEntity> seatEntities = new ArrayList<>();
        for(Area area: dataContainer.getData().getSeatingData().getAreas()) {
            for(Row row: area.getRows()) {
                for(Seat seat: row.getSeats()) {
                    seatEntities.add(parseSeatAndRowToSeatEntity(seat, row, sessionId));
                }
            }
        }
        return seatEntities;
    }

    private SeatEntity parseSeatAndRowToSeatEntity(Seat seat, Row row, int sessionId) {
        List<Warning> warningList = seat.getWarnings();
        String warningMessage = "";
        int warningCode = 0;
        if(warningList != null && warningList.size() > 0) {
            Warning warning = warningList.get(0);
            warningMessage = warning.getDescription();
            warningCode = warning.getCode();
        }
        return SeatEntity.builder()
                .id(Integer.valueOf(sessionId + String.valueOf(seat.getRowIndex()) + String.valueOf(seat.getColumnIndex())))
                .sessionId(sessionId)
                .rowIndex(seat.getRowIndex())
                .columnIndex(seat.getColumnIndex())
                .name(row.getName())
                .rowNumber(row.getRowNumber())
                .seatId(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .areaIndex(seat.getAreaIndex())
                .areaId(seat.getAreaId())
                .vistaAreaNumber(seat.getVistaAreaNumber())
                .vistaRowIndex(seat.getVistaRowIndex())
                .vistaColumnIndex(seat.getVistaColumnIndex())
                .priority(seat.getPriority())
                .defaultPriceInCents(seat.getDefaultPriceInCents())
                .seatNumber(seat.getSeatNumber())
                .seatStyle(seat.getSeatStyle())
                .seatDescription(seat.getSeatDescription())
                .seatStatus(seat.getSeatStatus())
                .tableStyle(seat.getTableStyle())
                .warningMessage(warningMessage)
                .warningCode(warningCode)
                .build();
    }
}
