package com.alamobot.services;

import com.alamobot.core.domain.MovieEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlamoScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlamoScheduler.class);

    @Autowired
    MovieService movieService;

    @Autowired
    SeatService seatService;

    @Autowired
    MarketService marketService;

    @Autowired
    CleanupService cleanupService;

//    @Scheduled(fixedRate = 15000)
    public void getMoviesFromAlamoAndPersist() {
        log.debug("Grabbed movie list from Alamo server and persisted");
        movieService.getMovieListFromServerAndPersist();
    }

//    @Scheduled(fixedRate = 15000)
    public void getMarketsFromAlamoAndPersist() {
        log.debug("Grabbed market list from Alamo server and persisted");
        marketService.getMarketListFromServerAndPersist();
    }

//    @Scheduled(fixedRate = 15000)
    public void getSeatsFromAlamoAndPersist() {
        List<MovieEntity> movieEntities = movieService.getWatchedMovieListFromDatabase();
        for(MovieEntity movieEntity: movieEntities) {
            if(movieEntity.getWatched()) {
                seatService.getSeatsFromServerAndPersist(movieEntity);
            }
        }
        log.debug("Grabbed seat list from Alamo server and persisted");
    }

//    @Scheduled(fixedRate = 15000)
    public void cleanUpOldShowtimes() {
        log.debug("Cleaning up database of old showtimes, seats and movies");
        cleanupService.cleanUpPastShowtimeData();
    }
}
