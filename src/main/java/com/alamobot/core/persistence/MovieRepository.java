package com.alamobot.core.persistence;

import com.alamobot.core.domain.MovieEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MovieRepository extends CrudRepository<MovieEntity, Integer> {
    List<MovieEntity> findAllByWatched(boolean watched);

    List<MovieEntity> findAllByFilmId(String filmId);
}
