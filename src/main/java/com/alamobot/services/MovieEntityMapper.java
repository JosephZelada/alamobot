package com.alamobot.services;

import com.alamobot.core.api.market.Cinema;
import com.alamobot.core.api.market.Date;
import com.alamobot.core.api.market.Film;
import com.alamobot.core.api.market.Format;
import com.alamobot.core.api.market.Market;
import com.alamobot.core.api.market.MarketContainer;
import com.alamobot.core.api.Movie;
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
import java.util.List;

class MovieEntityMapper {
    private MovieService movieService;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String ON_SALE_TAG = "onsale";

    MovieEntityMapper(MovieService movieService) {
        this.movieService = movieService;
    }

    List<Movie> marketToMovieList(MarketContainer marketContainer) {
        ArrayList<Movie> movies = new ArrayList<>();
        Market market = marketContainer.getMarket();
        LocalDateTime latestOnSaleDateSeenCurrentRun = movieService.getLatestOnSaleDateSeen();
        boolean filmIsSpecialEvent;
        boolean filmReleasedThisYearOrNext = true;
        for(Date date: market.getDates()) {
            LocalDate currentDate = LocalDate.parse(date.getDateId(), formatter);
            if(currentDate.atStartOfDay().isBefore(latestOnSaleDateSeenCurrentRun)) {
                continue;
            }
            if(latestOnSaleDateSeenCurrentRun.equals(LocalDateTime.MIN)) {
                latestOnSaleDateSeenCurrentRun = currentDate.atStartOfDay();
            }
            for(Cinema cinema: date.getCinemas()) {
                for(Film film: cinema.getFilms()) {
                    filmIsSpecialEvent = isFilmSpecialEvent(film.getFilmName());
                    //filmReleasedThisYearOrNext = isFilmReleaseDateThisOrNextYear(film.getFilmYear());
                    for(Series series: film.getSeries()) {
                        for(Format format: series.getFormats()) {
                            for(Session session: format.getSessions()) {
                                LocalDateTime movieDateTime = LocalDateTime.parse(session.getSessionDateTime());
                                if(movieDateTime.isBefore(movieService.getLatestOnSaleDateSeen())) {
                                    continue;
                                }
                                boolean filmTicketsOnSale = session.getSessionStatus().equals(ON_SALE_TAG);
                                if(shouldFilmUpdateLatestOnSaleDateSeen(
                                        movieDateTime,
                                        latestOnSaleDateSeenCurrentRun,
                                        filmIsSpecialEvent,
                                        filmReleasedThisYearOrNext) && filmTicketsOnSale){
                                    latestOnSaleDateSeenCurrentRun = movieDateTime;
                                }
                                movies.add(
                                        new Movie(
                                                session.getSessionId(),
                                                session.getSessionDateTime(),
                                                session.getSessionStatus(),
                                                new MarketEntity(market.getMarketId(), market.getMarketName(), market.getMarketSlug()),
                                                new CinemaEntity(cinema.getCinemaId(), cinema.getCinemaName(), cinema.getCinemaSlug()),
                                                new FilmEntity(film.getFilmId(), film.getFilmName(), film.getFilmSlug()),
                                                new FormatEntity(format.getFormatId(), format.getFormatName()),
                                                session.getSeatsLeft()
                                        )
                                );
                            }
                        }
                    }
                }
            }
        }
        movieService.setLatestOnSaleDateSeen(latestOnSaleDateSeenCurrentRun);
        return movies;
    }

    MovieEntity movieToMovieEntity(Movie movie) {
        return MovieEntity.builder()
                .sessionId(movie.getSessionId())
                .sessionDateTime(LocalDateTime.parse(movie.getSessionDateTime()))
                .sessionStatus(movie.getSessionStatus())
                .marketId(movie.getMarket().getId())
                .cinemaId(movie.getCinema().getId())
                .filmId(movie.getFilm().getId())
                .formatId(movie.getFormat().getId())
                .seatsLeft(movie.getSeatsLeft())
                .build();
    }

    private boolean isFilmReleaseDateThisOrNextYear(int filmReleaseYear) {
        return (filmReleaseYear != LocalDate.now().getYear()) || (filmReleaseYear != LocalDate.now().getYear() + 1);
    }

    private boolean shouldFilmUpdateLatestOnSaleDateSeen(
            LocalDateTime filmAirTime,
            LocalDateTime highWatermarkDate,
            boolean filmIsSpecialEvent,
            boolean filmReleasedThisYearOrNext) {
        return isFilmAirTimeNoMoreThanOneDayPastCurrentHighWatermarkDate(filmAirTime, highWatermarkDate)
                && !filmIsSpecialEvent
                && filmReleasedThisYearOrNext ;
    }

    private boolean isFilmAirTimeNoMoreThanOneDayPastCurrentHighWatermarkDate(LocalDateTime filmAirTime, LocalDateTime highWatermarkDate) {
        return filmAirTime.isAfter(highWatermarkDate) && highWatermarkDate.plusHours(25).isAfter(filmAirTime);
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
