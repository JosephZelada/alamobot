package com.alamobot.services;

import com.alamobot.core.api.FilmShowtime;
import com.alamobot.core.api.FilmShowtimes;
import com.alamobot.core.domain.FilmEntity;
import com.alamobot.core.domain.MovieEntity;
import com.alamobot.core.persistence.FilmRepository;
import com.alamobot.core.persistence.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FilmService {
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private MovieRepository movieRepository;

    public void markFilmAsViewed(String filmId, Boolean watched) {
        Optional<FilmEntity> filmEntityOptional = filmRepository.findById(filmId);
        if(!filmEntityOptional.isPresent()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FilmEntity filmEntity = filmEntityOptional.get();
        filmEntity.setWatched(watched);
        filmRepository.save(filmEntity);
    }

    //TODO: Figure out how to get this immediately as a list, not an iterable
    public List<FilmEntity> getAllFilms() {
        Iterable<FilmEntity> source = filmRepository.findAll();
        List<FilmEntity> target = new ArrayList<>();
        source.forEach(target::add);
        return target;
    }

    public List<FilmEntity> getAllFilmsForMarket(String marketId) {
        Iterable<FilmEntity> source = filmRepository.findAll();
        List<FilmEntity> target = new ArrayList<>();
        source.forEach(target::add);
        return target;
    }


    //TODO: Maybe use Dozer for the mapping
    public FilmShowtimes getAllFilmShowtimes(String filmId) {
        Optional<FilmEntity> filmEntityOptional = filmRepository.findById(filmId);
        if(!filmEntityOptional.isPresent()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FilmEntity filmEntity = filmEntityOptional.get();

        List<MovieEntity> movieEntityList = movieRepository.findAllByFilmId(filmId);
        List<FilmShowtime> filmShowtimes =  movieEntityList.stream()
                .map(movieEntity -> FilmShowtime.builder()
                        .sessionId(movieEntity.getSessionId())
                        .showtime(movieEntity.getSessionDateTime())
                        .watched(movieEntity.getWatched())
                        .build()
                ).collect(Collectors.toList());
        return FilmShowtimes.builder().name(filmEntity.getName()).showtimeList(filmShowtimes).build();
    }
}
