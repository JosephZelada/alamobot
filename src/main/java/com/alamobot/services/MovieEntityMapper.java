package com.alamobot.services;

import com.alamobot.core.api.Movie;
import com.alamobot.core.api.market.Cinema;
import com.alamobot.core.api.market.Film;
import com.alamobot.core.api.market.FilmDate;
import com.alamobot.core.api.market.Format;
import com.alamobot.core.api.market.Market;
import com.alamobot.core.api.market.MarketContainer;
import com.alamobot.core.api.market.Series;
import com.alamobot.core.api.market.Session;
import com.alamobot.core.domain.CinemaEntity;
import com.alamobot.core.domain.FilmEntity;
import com.alamobot.core.domain.FormatEntity;
import com.alamobot.core.domain.MarketEntity;
import com.alamobot.core.domain.MovieEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class MovieEntityMapper {
    private MovieService movieService;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String ON_SALE_TAG = "onsale";

    MovieEntityMapper(MovieService movieService) {
        this.movieService = movieService;
    }



    List<Movie> marketToMovieListStreams(MarketContainer marketContainer) {
        Market market = marketContainer.getMarket();
        Session returnedSession = new Session();
        returnedSession.setSessionDateTime(movieService.getHighWatermarkDate());
        Comparator<Session> sessionComparator = Comparator.comparing(Session::getSessionDateTime);
        Session tempSession = market.getDates().stream()
                .flatMap(filmDate -> filmDate.getCinemas().stream().map(
                        cinema -> cinema.getFilms().stream().map(
                                film -> film.getSeries().stream().map(
                                        series -> series.getFormats().stream().map(
                                                format -> format.getSessions().stream()
                                                        .filter(session -> !movieDateTimeIsBeforeCurrentHighWatermarkDate(session))
                                                        .filter(session -> doesHighWatermarkDateNeedToBeUpdated(session.getSessionDateTime(), movieService.getHighWatermarkDate(), film.getFilmName(), session.getSessionStatus()))
                                                        .max(sessionComparator)
                                                        .orElse(returnedSession)
                                        ).max(sessionComparator)
                                                .orElse(returnedSession)
                                ).max(sessionComparator)
                                        .orElse(returnedSession)
                        ).max(sessionComparator)
                                .orElse(returnedSession)
                )).max(sessionComparator)
                .orElse(returnedSession);
        LocalDateTime localHighWatermarkDate = tempSession.getSessionDateTime();
        List<Movie> movieList = market.getDates().stream()
                .flatMap(filmDate -> filmDate.getCinemas().stream().map(
                        cinema -> cinema.getFilms().stream().map(
                                film -> film.getSeries().stream().map(
                                        series -> series.getFormats().stream().map(
                                                format -> format.getSessions().stream()
                                                        .filter(session -> !movieDateTimeIsBeforeCurrentHighWatermarkDate(session))
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
        movieService.setHighWatermarkDate(localHighWatermarkDate);
        return movieList;
    }

    List<Movie> marketToMovieList(MarketContainer marketContainer) {
        ArrayList<Movie> movies = new ArrayList<>();
        Market market = marketContainer.getMarket();
        LocalDateTime localHighWatermarkDate = movieService.getHighWatermarkDate();
        for(FilmDate filmDate : market.getDates()) {
            LocalDate currentDate = LocalDate.parse(filmDate.getDateId(), formatter);
            if(currentDate.atStartOfDay().isBefore(localHighWatermarkDate)) {
                continue;
            }
            for(Cinema cinema: filmDate.getCinemas()) {
                for(Film film: cinema.getFilms()) {
                    for(Series series: film.getSeries()) {
                        for(Format format: series.getFormats()) {
                            for(Session session: format.getSessions()) {
                                LocalDateTime movieDateTime = session.getSessionDateTime();
                                //TODO: Filter with a stream, maybe look at optimus-prime for examples
                                if(movieDateTime.isBefore(movieService.getHighWatermarkDate())) {
                                    continue;
                                }
                                if(doesHighWatermarkDateNeedToBeUpdated(movieDateTime, localHighWatermarkDate, film.getFilmName(), session.getSessionStatus())) {
                                    localHighWatermarkDate = movieDateTime;
                                }
                                movies.add(combineToCreateMovie(session, market, cinema, film, format));
                            }
                        }
                    }
                }
            }
        }
        movieService.setHighWatermarkDate(localHighWatermarkDate);
        return movies;
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

    private boolean movieDateTimeIsBeforeCurrentHighWatermarkDate(Session session) {
        LocalDateTime movieDateTime = session.getSessionDateTime();
        if(movieDateTime.isBefore(movieService.getHighWatermarkDate())) {
            return true;
        }
        return false;
    }

    private boolean doesHighWatermarkDateNeedToBeUpdated(LocalDateTime movieDateTime, LocalDateTime localHighWatermarkDate, String filmName, String sessionStatus) {
        boolean filmTicketsOnSale = sessionStatus.equals(ON_SALE_TAG);
        if(shouldFilmUpdateLatestOnSaleDateSeen(
                movieDateTime,
                localHighWatermarkDate,
                isFilmSpecialEvent(filmName)
        ) && filmTicketsOnSale){
            return true;
        }
        return false;
    }

    private Movie combineToCreateMovie(Session session, Market market, Cinema cinema, Film film, Format format) {
        return Movie.builder()
                .sessionId(session.getSessionId())
                .sessionDateTime(session.getSessionDateTime())
                .sessionStatus(session.getSessionStatus())
                .market(new MarketEntity(market.getMarketId(), market.getMarketName(), market.getMarketSlug()))
                .cinema(new CinemaEntity(cinema.getCinemaId(), cinema.getCinemaName(), cinema.getCinemaSlug()))
                .film(new FilmEntity(film.getFilmId(), film.getFilmName(), film.getFilmSlug()))
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
