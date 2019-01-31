package com.alamobot.services;

import com.alamobot.core.AlamoUrls;
import com.alamobot.core.api.consume.Movie;
import com.alamobot.core.api.consume.showtime.MarketContainer;
import com.alamobot.core.domain.CinemaEntity;
import com.alamobot.core.domain.FilmEntity;
import com.alamobot.core.domain.FormatEntity;
import com.alamobot.core.domain.MarketEntity;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private Map<String, LocalDateTime> highWatermarkDateMap = new HashMap<>();

    public void markMovieAsViewed(int sessionId, Boolean watched) {
        Optional<MovieEntity> movieEntityOptional = movieRepository.findById(sessionId);
        if(!movieEntityOptional.isPresent()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        MovieEntity movieEntity = movieEntityOptional.get();
        movieEntity.setWatched(watched);
        movieRepository.save(movieEntity);
    }

    void getMovieListFromServerAndPersist() {
        marketRepository.findAllByWatched(true).forEach(market -> getMovieListFromServerAndPersistForMarket(market.getId()));
    }

    List<MovieEntity> getWatchedMovieListFromDatabase() {
        return movieRepository.findAllByWatched(true);
    }

    private void getMovieListFromServerAndPersistForMarket(String marketId) {
        highWatermarkDateMap.putIfAbsent(marketId, LocalDateTime.MIN);
        List<Movie> movieEntities = getMovieListFromServer(marketId);
        log.info("Found " + movieEntities.size() + " relevant films in the " + marketId + " market. Attempting to persist. The current high watermark date for on sale is " + highWatermarkDateMap.get(marketId));
        persistMovieList(movieEntities);
    }

    private List<Movie> getMovieListFromServer(String marketId) {
        String getMarketDetailsUrl = AlamoUrls.SHOWTIMES_FOR_MARKET_BASE_URL + marketId + "/";

        //TODO: Maybe swap restTemplate out with Feign?
        ResponseEntity<MarketContainer> marketContainerResponse =
                restTemplate.exchange(
                        getMarketDetailsUrl,
                        HttpMethod.GET,
                        null,
                        MarketContainer.class
                );
        MarketContainer marketContainer = marketContainerResponse.getBody();
        if(marketContainer == null) {
            log.info("No data received for market with ID " + marketId +" skipping");
            return new ArrayList<>();
        }
        return movieEntityMapper.marketToMovieList(marketContainer);
    }

    @Transactional
    private void persistMovieList(List<Movie> movieList) {
        extractAndPersistHelperObjects(movieList);
        //TODO: Make this a single batch call instead of every movie
        for(Movie movie: movieList) {
            MovieEntity movieEntity = convertToMovieEntity(movie);
            movieRepository.save(movieEntity);
        }
    }

    private void extractAndPersistHelperObjects(List<Movie> movieList) {
        extractAndPersistCinemas(movieList);
        extractAndPersistFilms(movieList);
        extractAndPersistFormats(movieList);
        extractAndPersistMarkets(movieList);
    }

    private void extractAndPersistMarkets(List<Movie> movieList) {
        Set<MarketEntity> marketSet =  movieList.stream()
                .map(movie -> convertToMarketEntity(movie))
                .distinct()
                .collect(Collectors.toSet());
        marketRepository.saveAll(marketSet);
    }

    private void extractAndPersistCinemas(List<Movie> movieList) {
        Set<CinemaEntity> cinemaSet =  movieList.stream()
                .map(movie -> convertToCinemaEntity(movie))
                .distinct()
                .collect(Collectors.toSet());
        cinemaRepository.saveAll(cinemaSet);
    }

    private void extractAndPersistFilms(List<Movie> movieList) {
        Set<FilmEntity> filmSet =  movieList.stream()
                .map(movie -> convertToFilmEntity(movie))
                .distinct()
                .collect(Collectors.toSet());
        filmRepository.saveAll(filmSet);
    }

    private void extractAndPersistFormats(List<Movie> movieList) {
        Set<FormatEntity> formatSet =  movieList.stream()
                .map(movie -> FormatEntity.builder()
                        .id(movie.getFormat().getId())
                        .name(movie.getFormat().getName())
                        .build()
                )
                .distinct()
                .collect(Collectors.toSet());
        formatRepository.saveAll(formatSet);
    }

    private MovieEntity convertToMovieEntity(Movie movie) {
        MovieEntity movieEntity = movieEntityMapper.movieToMovieEntity(movie);
        Optional<MovieEntity> movieEntityOptional = movieRepository.findById(movie.getSessionId());
        boolean watchedStatus = movieEntityOptional.isPresent() ? movieEntityOptional.get().getWatched() : false;
        movieEntity.setWatched(watchedStatus);
        return movieEntity;
    }

    private CinemaEntity convertToCinemaEntity(Movie movie) {
        CinemaEntity cinemaEntity = CinemaEntity.builder()
                .id(movie.getCinema().getId())
                .name(movie.getCinema().getName())
                .slug(movie.getCinema().getSlug())
                .marketId(movie.getMarket().getId())
                .build();
        Optional<CinemaEntity> cinemaEntityOptional = cinemaRepository.findById(movie.getCinema().getId());
        boolean watchedStatus = cinemaEntityOptional.isPresent() ? cinemaEntityOptional.get().getWatched() : false;
        cinemaEntity.setWatched(watchedStatus);
        return cinemaEntity;
    }

    private MarketEntity convertToMarketEntity(Movie movie) {
        MarketEntity marketEntity = MarketEntity.builder()
                .id(movie.getMarket().getId())
                .name(movie.getMarket().getName())
                .slug(movie.getMarket().getSlug())
                .build();
        Optional<MarketEntity> marketEntityOptional = marketRepository.findById(movie.getMarket().getId());
        boolean watchedStatus = marketEntityOptional.isPresent() ? marketEntityOptional.get().getWatched() : false;
        marketEntity.setWatched(watchedStatus);
        return marketEntity;
    }

    private FilmEntity convertToFilmEntity(Movie movie) {
        FilmEntity filmEntity = FilmEntity.builder()
                .id(movie.getFilm().getId())
                .name(movie.getFilm().getName())
                .slug(movie.getFilm().getSlug())
                .build();
        Optional<FilmEntity> filmEntityOptional = filmRepository.findById(movie.getFilm().getId());
        boolean watchedStatus = filmEntityOptional.isPresent() ? filmEntityOptional.get().getWatched() : false;
        filmEntity.setWatched(watchedStatus);
        return filmEntity;
    }
}
