package com.alamobot.services;

import com.alamobot.core.domain.CinemaEntity;
import com.alamobot.core.persistence.CinemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Optional;

public class CinemaService extends PageableSearchableService {
    @Autowired
    private CinemaRepository cinemaRepository;

    public void markCinemaAsViewed(String cinemaId, Boolean watched) {
        Optional<CinemaEntity> cinemaEntityOptional = cinemaRepository.findById(cinemaId);
        if(!cinemaEntityOptional.isPresent()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        CinemaEntity cinemaEntity = cinemaEntityOptional.get();
        cinemaEntity.setWatched(watched);
        cinemaRepository.save(cinemaEntity);
    }

    public Page<CinemaEntity> getAllCinemasForMarket(String sortBy, String orderBy, Integer pageNumber, Integer pageSize, String cinemaName, String marketId) {
        SearchCriteria criteria = buildSearchCriteria(cinemaName, sortBy, orderBy, pageNumber, pageSize);
        Pageable pageable = PageRequest.of(criteria.getPageNumber(), criteria.getPageSize(), criteria.getSort());
        return cinemaRepository.findAllByMarketId(marketId, pageable);
    }
}
