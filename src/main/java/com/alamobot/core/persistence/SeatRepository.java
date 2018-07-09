package com.alamobot.core.persistence;

import com.alamobot.core.domain.SeatEntity;
import org.springframework.data.repository.CrudRepository;

public interface SeatRepository extends CrudRepository<SeatEntity, Integer> {
}
