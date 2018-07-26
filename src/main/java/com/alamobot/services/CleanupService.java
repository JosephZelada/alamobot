package com.alamobot.services;

import com.alamobot.core.domain.FilmEntity;
import com.alamobot.core.domain.MovieEntity;
import com.alamobot.core.persistence.FilmRepository;
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

    @Transactional
    public void cleanUpPastShowtimeData() {
        cleanUpMovies();
        cleanUpFilms();
    }

    private void cleanUpMovies() {
        List<MovieEntity> movieEntitiesToDeleteList = new ArrayList<>();
        for(MovieEntity movieEntity: movieRepository.findAll()) {
            if(movieEntity.getSessionDateTime().isBefore(LocalDateTime.now())) {
                cleanUpSeats(movieEntity.getSessionId());
                movieEntitiesToDeleteList.add(movieEntity);
            }
        }
        movieRepository.deleteAll(movieEntitiesToDeleteList);
    }

    private void cleanUpFilms() {
        List<FilmEntity> filmEntitiesToDeleteList = new ArrayList<>();
        for(FilmEntity filmEntity: filmRepository.findAll()) {
            if(movieRepository.findAllByFilmId(filmEntity.getId()).size() == 0){
                filmEntitiesToDeleteList.add(filmEntity);
            }
        }
        filmRepository.deleteAll(filmEntitiesToDeleteList);
    }

    private void cleanUpSeats(int movieSessionId) {
        seatRepository.deleteBySessionId(movieSessionId);
    }
}
