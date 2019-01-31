package com.alamobot.core.persistence;

import com.alamobot.core.domain.FilmEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface FilmRepository extends EntityPagingAndSortingRepository<FilmEntity> {
    Page<FilmEntity> findAllByIdIn(Set<String> idList, Pageable pageable);
}
