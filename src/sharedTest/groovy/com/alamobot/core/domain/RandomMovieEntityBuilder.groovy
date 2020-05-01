package com.alamobot.core.domain

import com.alamobot.core.persistence.MovieRepository

import java.time.LocalDateTime

import static com.alamobot.core.CoreARandom.aRandom

class RandomMovieEntityBuilder extends MovieEntity.MovieEntityBuilder {
    private MovieRepository movieRepository

    RandomMovieEntityBuilder(MovieRepository movieRepository) {
        super.cinemaId(aRandom.alamoId())
                .filmId(aRandom.alamoId())
                .formatId(aRandom.alamoId())
                .marketId(aRandom.alamoId())
                .sessionDateTime(LocalDateTime.now())
                .sessionStatus("CHANGEME")
                .watched(aRandom.coinFlip())
                .seatsLeft(aRandom.intBetween(0, 300))
                .sessionId(aRandom.intId())
        this.movieRepository = movieRepository
    }

    MovieEntity save() {
        MovieEntity entity = build()
        return movieRepository.save(entity)
    }
}
