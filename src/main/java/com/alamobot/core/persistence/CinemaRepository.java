package com.alamobot.core.persistence;

import com.alamobot.core.domain.CinemaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CinemaRepository extends PagingAndSortingRepository<CinemaEntity, String> {
    Page<CinemaEntity> findAllByMarketId(String marketId, Pageable pageable);
}
