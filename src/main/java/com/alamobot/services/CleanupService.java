package com.alamobot.services;

import com.alamobot.core.domain.FilmEntity;
import com.alamobot.core.domain.MarketEntity;
import com.alamobot.core.domain.MovieEntity;
import com.alamobot.core.persistence.FilmRepository;
import com.alamobot.core.persistence.MarketRepository;
import com.alamobot.core.persistence.MovieRepository;
import com.alamobot.core.persistence.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CleanupService {
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private MarketRepository marketRepository;

    @Transactional
    public void cleanUpPastShowtimeData() {
        cleanUpMarkets();
        cleanUpMovies();
        cleanUpFilms();
    }

    private void cleanUpMarkets() {
        List<MarketEntity> marketEntitiesToCleanUp = marketRepository.findAllByWatched(false);
        marketEntitiesToCleanUp.forEach(marketEntity -> cleanUpMoviesForMarket(marketEntity.getId()));
    }

    private void cleanUpMovies() {
        //TODO: Combine with cleanUpMoviesForMarket
        //TODO: Don't delete movies with seats unpaid/bought
        List<MovieEntity> movieEntitiesToDeleteList = new ArrayList<>();
        for(MovieEntity movieEntity: movieRepository.findAll()) {
            if(showtimeHasSeatsThatHaveBeenBoughtByMe(movieEntity.getSessionId())) {
                if(movieEntity.getSessionDateTime().isBefore(LocalDateTime.now())) {
                    cleanUpSeats(movieEntity.getSessionId());
                    movieEntitiesToDeleteList.add(movieEntity);
                }
                if(!movieEntity.getWatched()) {
                    cleanUpSeats(movieEntity.getSessionId());
                }
            }

        }
        movieRepository.deleteAll(movieEntitiesToDeleteList);
    }

    private void cleanUpMoviesForMarket(String marketId) {
        List<MovieEntity> movieEntitiesToDeleteList = new ArrayList<>();
        for(MovieEntity movieEntity: movieRepository.findAllByMarketId(marketId)) {
            if(showtimeHasSeatsThatHaveBeenBoughtByMe(movieEntity.getSessionId())) {
                cleanUpSeats(movieEntity.getSessionId());
                movieEntitiesToDeleteList.add(movieEntity);
            }
        }
        movieRepository.deleteAll(movieEntitiesToDeleteList);
    }

    private void cleanUpFilms() {
        List<FilmEntity> filmEntitiesToDeleteList = new ArrayList<>();
        for(FilmEntity filmEntity: filmRepository.findAll()) {
            if(movieHasValidShowtimesInDatabase(filmEntity.getId())){
                filmEntitiesToDeleteList.add(filmEntity);
            }
        }
        filmRepository.deleteAll(filmEntitiesToDeleteList);
    }

    private void cleanUpSeats(int movieSessionId) {
        seatRepository.deleteBySessionId(movieSessionId);
    }

    private boolean movieHasValidShowtimesInDatabase(String filmId) {
        return movieRepository.findAllByFilmId(filmId).size() == 0;
    }

    private boolean showtimeHasSeatsThatHaveBeenBoughtByMe(Integer sessionId) {
        return seatRepository.findAllBySessionIdAndSeatBought(sessionId, true).size() == 0;
    }
}
