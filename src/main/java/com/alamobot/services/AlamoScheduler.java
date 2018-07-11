package com.alamobot.services;

import com.alamobot.core.domain.MovieEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlamoScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlamoScheduler.class);

    @Autowired
    MovieService movieService;

    @Autowired
    SeatService seatService;

    @Scheduled(fixedRate = 30000)
    public void getMoviesFromAlamoAndPersist() {
        log.info("Grabbed movie list from Alamo server and persisted");
        movieService.getMovieListFromServerAndPersist();
    }

    @Scheduled(fixedRate = 60000)
    public void getSeatsFromAlamoAndPersist() {
        List<MovieEntity> movieEntities = movieService.getWatchedMovieListFromDatabase();
        for(MovieEntity movieEntity: movieEntities) {
            seatService.getSeatsFromServerAndPersist(movieEntity);
        }
        log.info("Grabbed seat list from Alamo server and persisted");
    }
    //Cleanup database
}
