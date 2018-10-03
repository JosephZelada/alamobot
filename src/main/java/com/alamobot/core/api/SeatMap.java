package com.alamobot.core.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//TODO: Maybe replace the seats arraylist of arraylists with a concrete class
public class SeatMap {
    private String filmName;
    private Integer theaterNum;
    private ArrayList<ArrayList<Seat>> seats;
}
