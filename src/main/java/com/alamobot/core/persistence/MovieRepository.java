package com.alamobot.core.persistence;

import com.alamobot.core.domain.MovieEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MovieRepository extends CrudRepository<MovieEntity, Integer> {
    List<MovieEntity> findAllByWatched(boolean watched);

    List<MovieEntity> findAllByFilmId(String filmId);

    List<MovieEntity> findAllByFilmIdAndMarketId(String filmId, String marketId);

    List<MovieEntity> findAllByFilmIdAndCinemaId(String filmId, String cinemaId);

    List<MovieEntity> findAllByMarketId(String marketId);

    MovieEntity findBySessionId(int sessionId);

    @Transactional
    void deleteAllByMarketId(String marketId);
}
