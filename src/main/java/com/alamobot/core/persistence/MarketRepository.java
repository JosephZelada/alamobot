package com.alamobot.core.persistence;

import com.alamobot.core.domain.MarketEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MarketRepository extends CrudRepository<MarketEntity, String> {
    List<MarketEntity> findAllByWatched(boolean watched);
}
