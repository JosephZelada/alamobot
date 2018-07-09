package com.alamobot.services;

import com.alamobot.core.AlamoUrls;
import com.alamobot.core.api.Movie;
import com.alamobot.core.api.market.MarketContainer;
import com.alamobot.core.domain.MovieEntity;
import com.alamobot.core.persistence.CinemaRepository;
import com.alamobot.core.persistence.FilmRepository;
import com.alamobot.core.persistence.FormatRepository;
import com.alamobot.core.persistence.MarketRepository;
import com.alamobot.core.persistence.MovieRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MarketRepository marketRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private FormatRepository formatRepository;

    private MovieEntityMapper movieEntityMapper = new MovieEntityMapper(this);
    private RestTemplate restTemplate = new RestTemplate();

    @Getter
    @Setter
    private LocalDateTime latestOnSaleDateSeen = LocalDateTime.MIN;

    List<MovieEntity> getWatchedMovieListFromDatabase() {
        List<MovieEntity> movieEntities = new ArrayList<>();
        movieRepository.findByWatched(true).forEach(movieEntities::add);
        return movieEntities;
    }

    void getMovieListFromServerAndPersist() {
        getMovieListFromServerAndPersistForMarket("0000");
    }

    private void getMovieListFromServerAndPersistForMarket(String marketId) {
        List<Movie> movieEntities = getMovieListFromServer(marketId);
        log.info("Found " + movieEntities.size() + " relevant films in the " + marketId + " market. Attempting to persist. The current high watermark date for on sale is " + latestOnSaleDateSeen);
        persistMovieList(movieEntities);
    }

    private List<Movie> getMovieListFromServer(String marketId) {
        String getMarketDetailsUrl = AlamoUrls.MARKET_BASE_URL + marketId + "/";

        ResponseEntity<MarketContainer> marketContainerResponse =
                restTemplate.exchange(
                        getMarketDetailsUrl,
                        HttpMethod.GET,
                        null,
                        MarketContainer.class
                );
        MarketContainer marketContainers = marketContainerResponse.getBody();
        return movieEntityMapper.marketToMovieList(marketContainers);
    }

    private void persistMovieList(List<Movie> movies) {
        for(Movie movie: movies) {
            persistHelperObjects(movie);
            MovieEntity movieEntity = convertToMovieEntity(movie);
            movieRepository.save(movieEntity);
        }
    }

    private MovieEntity convertToMovieEntity(Movie movie) {
        MovieEntity movieEntity = movieEntityMapper.movieToMovieEntity(movie);
        Optional<MovieEntity> movieEntityOptional = movieRepository.findById(movie.getSessionId());
        boolean watchedStatus = movieEntityOptional.isPresent() ? movieEntityOptional.get().getWatched() : false;
        movieEntity.setWatched(watchedStatus);
        return movieEntity;
    }

    private void persistHelperObjects(Movie movie) {
        marketRepository.save(movie.getMarket());
        cinemaRepository.save(movie.getCinema());
        filmRepository.save(movie.getFilm());
        formatRepository.save(movie.getFormat());
    }
}
