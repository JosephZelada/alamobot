package com.alamobot.core.persistence;

import com.alamobot.core.domain.PagingAndSortingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

@NoRepositoryBean
public interface EntityPagingAndSortingRepository<T extends PagingAndSortingEntity> extends PagingAndSortingRepository<T, String> {
    List<T> findAllByWatched(boolean watched);

    Page<T> findAllByNameContainingIgnoreCase(String marketName, Pageable pageable);
}