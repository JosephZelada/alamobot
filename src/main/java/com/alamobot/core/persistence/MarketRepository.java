package com.alamobot.core.persistence;

import com.alamobot.core.domain.MarketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MarketRepository extends PagingAndSortingRepository<MarketEntity, String> {
    List<MarketEntity> findAllByWatched(boolean watched);

    Page<MarketEntity> findAllByNameContainingIgnoreCase(String marketName, Pageable pageable);
}
