package com.alamobot.services;

import com.alamobot.core.domain.CinemaEntity;
import com.alamobot.core.domain.MovieEntity;
import com.alamobot.core.persistence.CinemaRepository;
import com.alamobot.core.persistence.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CinemaService extends PageableSearchableService {
    @Autowired
    private CinemaRepository cinemaRepository;
    @Autowired
    private MovieRepository movieRepository;

    public boolean allCinemasExist(Set<String> cinemas) {
        for(String cinemaId: cinemas) {
            if(!cinemaRepository.findById(cinemaId).isPresent()) {
                return false;
            }
        }
        return true;
    }

    public void markCinemaAsViewed(String cinemaId, Boolean watched) {
        Optional<CinemaEntity> cinemaEntityOptional = cinemaRepository.findById(cinemaId);
        if(!cinemaEntityOptional.isPresent()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        CinemaEntity cinemaEntity = cinemaEntityOptional.get();
        cinemaEntity.setWatched(watched);
        cinemaRepository.save(cinemaEntity);
    }

    public Page<CinemaEntity> getAllCinemasForFilmInMarket(String sortBy,
                                                           String orderBy,
                                                           Integer pageNumber,
                                                           Integer pageSize,
                                                           String cinemaName,
                                                           String marketId,
                                                           String filmId) {
        List<MovieEntity> movieList = movieRepository.findAllByFilmIdAndMarketId(filmId, marketId);
        HashSet<String> uniqueCinemaSet = new HashSet<>();
        for(MovieEntity movieEntity: movieList) {
            uniqueCinemaSet.add(movieEntity.getCinemaId());
        }
        List<String> cinemasInMarketWithFilmPlaying = new ArrayList<>(uniqueCinemaSet);
        SearchCriteria criteria = buildSearchCriteria(cinemaName, sortBy, orderBy, pageNumber, pageSize);
        Pageable pageable = PageRequest.of(criteria.getPageNumber(), criteria.getPageSize(), criteria.getSort());
        return cinemaRepository.findAllByIdIn(cinemasInMarketWithFilmPlaying, pageable);
    }
}
