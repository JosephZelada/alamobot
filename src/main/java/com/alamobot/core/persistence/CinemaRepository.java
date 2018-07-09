package com.alamobot.core.persistence;

import com.alamobot.core.domain.CinemaEntity;
import org.springframework.data.repository.CrudRepository;

public interface CinemaRepository extends CrudRepository<CinemaEntity, String> {
}
