package com.alamobot.core.persistence;

import com.alamobot.core.domain.MarketEntity;
import org.springframework.data.repository.CrudRepository;

public interface MarketRepository extends CrudRepository<MarketEntity, String> {
}
