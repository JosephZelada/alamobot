package com.alamobot.services;

import com.alamobot.core.api.consume.Movie;
import com.alamobot.core.api.consume.showtime.Cinema;
import com.alamobot.core.api.consume.showtime.Film;
import com.alamobot.core.api.consume.showtime.Format;
import com.alamobot.core.api.consume.showtime.Market;
import com.alamobot.core.api.consume.showtime.MarketContainer;
import com.alamobot.core.api.consume.showtime.Session;
import com.alamobot.core.domain.CinemaEntity;
import com.alamobot.core.domain.FilmEntity;
import com.alamobot.core.domain.FormatEntity;
import com.alamobot.core.domain.MarketEntity;
import com.alamobot.core.domain.MovieEntity;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class MovieEntityMapper {
    @Autowired
    private HighWatermarkService highWatermarkService;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String ON_SALE_TAG = "onsale";

    List<Movie> marketToMovieList(MarketContainer marketContainer) {
        Market market = marketContainer.getMarket();
        String marketId = market.getMarketId();
        LocalDateTime highWatermarkDate = highWatermarkService.getHighWatermarkForMarket(market.getMarketId());
        List<Movie> movieList = market.getDates().stream()
                .filter(filmDate -> !LocalDate.parse(filmDate.getDateId(), formatter).atStartOfDay().isBefore(highWatermarkDate))
                .flatMap(filmDate -> filmDate.getCinemas().stream()
                        .map(cinema -> cinema.getFilms().stream()
                                .map(film -> film.getSeries().stream()
                                        .map(series -> series.getFormats().stream()
                                                .map(format -> format.getSessions().stream()
                                                        .filter(session -> !movieDateTimeIsBeforeCurrentHighWatermarkDate(session.getSessionDateTime(), marketId))
                                                        .map(session -> combineToCreateMovie(session, market, cinema, film, format))
                                                        .collect(Collectors.toList())
                                                )
                                                .flatMap(List::stream)
                                                .collect(Collectors.toList())
                                        )
                                        .flatMap(List::stream)
                                        .collect(Collectors.toList())
                                )
                                .flatMap(List::stream)
                                .collect(Collectors.toList())
                        ))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        updateMovieServiceWithNewHighWatermarkDate(movieList, marketId);
        return movieList;
    }

    //TODO: Maybe try to replace for loop with streams, kinda difficult
    private void updateMovieServiceWithNewHighWatermarkDate(List<Movie> movieList, String marketId) {
        LocalDateTime localHighWatermarkDate = highWatermarkService.getHighWatermarkForMarket(marketId);
        for(Movie movie: movieList) {
            if(doesHighWatermarkDateNeedToBeUpdated(movie.getSessionDateTime(), localHighWatermarkDate, movie.getFilm().getName(), movie.getSessionStatus())) {
                localHighWatermarkDate = movie.getSessionDateTime();
            }
        }
//        LocalDateTime localHighWatermarkDate = movieList.stream()
//                .filter(movie -> !movieDateTimeIsBeforeCurrentHighWatermarkDate(movie.getSessionDateTime()))
//                .filter(movie -> doesHighWatermarkDateNeedToBeUpdated(movie.getSessionDateTime(), movieService.getHighWatermarkDate(), movie.getFilm().getName(), movie.getSessionStatus()))
//                .max(Comparator.comparing(Movie::getSessionDateTime))
//                .orElse(Movie.builder().sessionDateTime(movieService.getHighWatermarkDate()).build())
//                .getSessionDateTime();

        highWatermarkService.updateHighWatermarkForMarket(marketId, localHighWatermarkDate);
    }

    MovieEntity movieToMovieEntity(Movie movie) {
        return MovieEntity.builder()
                .sessionId(movie.getSessionId())
                .sessionDateTime(movie.getSessionDateTime())
                .sessionStatus(movie.getSessionStatus())
                .marketId(movie.getMarket().getId())
                .cinemaId(movie.getCinema().getId())
                .filmId(movie.getFilm().getId())
                .formatId(movie.getFormat().getId())
                .seatsLeft(movie.getSeatsLeft())
                .build();
    }

    private boolean movieDateTimeIsBeforeCurrentHighWatermarkDate(LocalDateTime movieDateTime, String marketId) {
        return movieDateTime.isBefore(highWatermarkService.getHighWatermarkForMarket(marketId));
    }

    private boolean doesHighWatermarkDateNeedToBeUpdated(LocalDateTime movieDateTime, LocalDateTime localHighWatermarkDate, String filmName, String sessionStatus) {
        boolean filmTicketsOnSale = sessionStatus.equals(ON_SALE_TAG);
        return shouldFilmUpdateLatestOnSaleDateSeen(movieDateTime, localHighWatermarkDate, isFilmSpecialEvent(filmName)) && filmTicketsOnSale;
    }

    private Movie combineToCreateMovie(Session session, Market market, Cinema cinema, Film film, Format format) {
        return Movie.builder()
                .sessionId(session.getSessionId())
                .sessionDateTime(session.getSessionDateTime())
                .sessionStatus(session.getSessionStatus())
                .market(MarketEntity.builder()
                                .id(market.getMarketId())
                                .name(market.getMarketName())
                                .slug(market.getMarketSlug())
                                .build())
                .cinema(CinemaEntity.builder()
                                .id(cinema.getCinemaId())
                                .name(cinema.getCinemaName())
                                .slug(cinema.getCinemaSlug())
                                .marketId(market.getMarketId())
                                .build())
                .film(FilmEntity.builder()
                              .id(film.getFilmId())
                              .name(film.getFilmName())
                              .slug(film.getFilmSlug())
                              .build())
                .format(new FormatEntity(format.getFormatId(), format.getFormatName()))
                .seatsLeft(session.getSeatsLeft())
                .build();
    }

    private boolean shouldFilmUpdateLatestOnSaleDateSeen(LocalDateTime filmAirTime, LocalDateTime highWatermarkDate, boolean filmIsSpecialEvent) {
        return isFilmAirTimeNoMoreThanOneDayPastCurrentHighWatermarkDate(filmAirTime, highWatermarkDate)
                && !filmIsSpecialEvent;
    }

    private boolean isFilmAirTimeNoMoreThanOneDayPastCurrentHighWatermarkDate(LocalDateTime filmAirTime, LocalDateTime highWatermarkDate) {
        return (filmAirTime.isAfter(highWatermarkDate) && highWatermarkDate.plusHours(25).isAfter(filmAirTime)) || highWatermarkDate.equals(LocalDateTime.MIN);
    }

    //If a film name contains one or more lowercase character, that is a special event and should not record for lastOnSaleDateSeen
    private boolean isFilmSpecialEvent(String filmName) {
        for(char ch: filmName.toCharArray()) {
            if (Character.isLowerCase(ch)) {
                return true;
            }
        }
        return false;
    }
}
