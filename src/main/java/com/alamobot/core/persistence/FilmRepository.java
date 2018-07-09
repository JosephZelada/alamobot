package com.alamobot.core.persistence;

import com.alamobot.core.domain.FilmEntity;
import org.springframework.data.repository.CrudRepository;

public interface FilmRepository extends CrudRepository<FilmEntity, String> {
}
