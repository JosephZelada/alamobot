package com.alamobot.services;

import com.alamobot.core.persistence.CinemaRepository;
import com.alamobot.core.persistence.FilmRepository;
import com.alamobot.core.persistence.FormatRepository;
import com.alamobot.core.persistence.MarketRepository;
import com.alamobot.core.persistence.MovieRepository;
import com.alamobot.core.persistence.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class CleanupService {
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MarketRepository marketRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private FormatRepository formatRepository;

    public void cleanUpPastShowtimeData() {
        //Clean up all seat entries for past showtimes that have been paid
        //Clean up all movie entries with showtimes in the past that have all been paid in full
        //Clean up all film entries with no remaining movie entries
    }
}
