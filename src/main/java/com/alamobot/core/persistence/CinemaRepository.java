package com.alamobot.core.persistence;

import com.alamobot.core.domain.CinemaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface CinemaRepository extends PagingAndSortingRepository<CinemaEntity, String> {
    Page<CinemaEntity> findAllByMarketId(String marketId, Pageable pageable);

    List<CinemaEntity> findAllByMarketId(String marketId);

    Page<CinemaEntity> findAllByIdIn(List<String> ids, Pageable pageable);

    Optional<CinemaEntity> findByName(String cinemaName);
}
