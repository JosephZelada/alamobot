package com.alamobot.services;

import com.alamobot.core.api.FilmShowtime;
import com.alamobot.core.api.FilmShowtimes;
import com.alamobot.core.domain.CinemaEntity;
import com.alamobot.core.domain.FilmEntity;
import com.alamobot.core.domain.FilmWithSeatCount;
import com.alamobot.core.domain.MovieEntity;
import com.alamobot.core.domain.SeatEntity;
import com.alamobot.core.persistence.CinemaRepository;
import com.alamobot.core.persistence.FilmRepository;
import com.alamobot.core.persistence.MarketRepository;
import com.alamobot.core.persistence.MovieRepository;
import com.alamobot.core.persistence.SeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class FilmService extends PageableSearchableService {
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CinemaRepository cinemaRepository;
    @Autowired
    private MarketRepository marketRepository;
    @Autowired
    private SeatRepository seatRepository;

    public void markFilmAsViewed(String filmId, Boolean watched) {
        Optional<FilmEntity> filmEntityOptional = filmRepository.findById(filmId);
        if(!filmEntityOptional.isPresent()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FilmEntity filmEntity = filmEntityOptional.get();
        filmEntity.setWatched(watched);
        filmRepository.save(filmEntity);
    }

    public Page<FilmEntity> getAllFilms(String marketName, String sortBy, String orderBy, Integer pageNumber, Integer pageSize) {
        return getAllEntities(marketName, sortBy, orderBy, pageNumber, pageSize, filmRepository);
    }

    public Collection<FilmWithSeatCount> getAllFilmsWithBoughtSeats() {
        HashMap<Integer, FilmWithSeatCount> sessionSeatCountMap = new HashMap<>();
        for(SeatEntity seatEntity: seatRepository.findAllBySeatBought(true)) {
            FilmWithSeatCount filmWithSeatCount = sessionSeatCountMap.get(seatEntity.getSessionId());
            if(filmWithSeatCount != null) {
                filmWithSeatCount.incrementSeatCount();
            } else {
                filmWithSeatCount = FilmWithSeatCount.builder().sessionId(seatEntity.getSessionId()).seatCount(1).build();
            }
            sessionSeatCountMap.put(seatEntity.getSessionId(), filmWithSeatCount);
        }
        for(Integer sessionId: sessionSeatCountMap.keySet()) {
            MovieEntity movieEntity = movieRepository.findBySessionId(sessionId);
            Optional<CinemaEntity> cinemaEntityOptional = cinemaRepository.findById(movieEntity.getCinemaId());
            if(!cinemaEntityOptional.isPresent()) {
                log.error("Could not find cinema entry for showtime session " + sessionId);
                sessionSeatCountMap.remove(sessionId);
                continue;
            }
            CinemaEntity cinemaEntity = cinemaEntityOptional.get();
            String filmId = movieEntity.getFilmId();
            Optional<FilmEntity> filmEntityOptional = filmRepository.findById(filmId);
            if(!filmEntityOptional.isPresent()) {
                log.error("Could not find movie entry for showtime session " + sessionId);
                sessionSeatCountMap.remove(sessionId);
                continue;
            }
            FilmEntity filmEntity = filmEntityOptional.get();
            FilmWithSeatCount filmWithSeatCount = sessionSeatCountMap.get(sessionId);
            filmWithSeatCount.setFilmName(filmEntity.getName());
            filmWithSeatCount.setSessionDateTime(movieEntity.getSessionDateTime());
            filmWithSeatCount.setCinema(cinemaEntity.getName());
        }
        return sessionSeatCountMap.values();
    }

    //TODO: Refactor to join film and movie tables rather than doing 2 gets
    //TODO: Re-enable searching in repository
    public Page<FilmEntity> getAllFilmsForMarket(String sortBy, String orderBy, Integer pageNumber, Integer pageSize, String marketId, String cinemaName) {
        List<MovieEntity> movieShowtimeList = movieRepository.findAllByMarketId(marketId);
        HashSet<String> filmIdList = new HashSet<>();
        movieShowtimeList.forEach(movieEntity -> {
            filmIdList.add(movieEntity.getFilmId());
        });
        SearchCriteria criteria = buildSearchCriteria(cinemaName, sortBy, orderBy, pageNumber, pageSize);
        Pageable pageable = PageRequest.of(criteria.getPageNumber(), criteria.getPageSize(), criteria.getSort());
        return filmRepository.findAllByIdIn(filmIdList, pageable);
    }

    //TODO: Maybe use Dozer for the mapping
    public FilmShowtimes getAllFilmShowtimes(String filmId) {
        Optional<FilmEntity> filmEntityOptional = filmRepository.findById(filmId);
        if(!filmEntityOptional.isPresent()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FilmEntity filmEntity = filmEntityOptional.get();

        List<MovieEntity> movieEntityList = movieRepository.findAllByFilmId(filmId);
        List<FilmShowtime> filmShowtimes =  movieEntityList.stream()
                .map(movieEntity -> FilmShowtime.builder()
                        .sessionId(movieEntity.getSessionId())
                        .showtime(movieEntity.getSessionDateTime())
                        .watched(movieEntity.getWatched())
                        .build()
                ).collect(Collectors.toList());
        return FilmShowtimes.builder().name(filmEntity.getName()).showtimeList(filmShowtimes).build();
    }
}
