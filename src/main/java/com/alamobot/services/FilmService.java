package com.alamobot.services;

import com.alamobot.core.domain.FilmEntity;
import com.alamobot.core.persistence.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Optional;

public class FilmService {
    @Autowired
    private FilmRepository filmRepository;

    public void markFilmAsViewed(String filmId) {
        Optional<FilmEntity> filmEntityOptional = filmRepository.findById(filmId);
        if(!filmEntityOptional.isPresent()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FilmEntity filmEntity = filmEntityOptional.get();
        filmEntity.setWatched(true);
        filmRepository.save(filmEntity);
    }
}
