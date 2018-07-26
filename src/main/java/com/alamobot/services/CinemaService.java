package com.alamobot.services;

import com.alamobot.core.domain.CinemaEntity;
import com.alamobot.core.persistence.CinemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Optional;

public class CinemaService {
    @Autowired
    private CinemaRepository cinemaRepository;

    public void markCinemaAsViewed(String cinemaId) {
        Optional<CinemaEntity> cinemaEntityOptional = cinemaRepository.findById(cinemaId);
        if(!cinemaEntityOptional.isPresent()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        CinemaEntity cinemaEntity = cinemaEntityOptional.get();
        cinemaEntity.setWatched(true);
        cinemaRepository.save(cinemaEntity);
    }
}
