package com.alamobot.services;

import com.alamobot.core.api.Seat;
import com.alamobot.core.domain.FilmAlertEntity;
import com.alamobot.core.domain.FilmEntity;
import com.alamobot.core.domain.MovieEntity;
import com.alamobot.core.domain.SeatEntity;
import com.alamobot.core.persistence.AlertRepository;
import com.alamobot.core.persistence.FilmRepository;
import com.alamobot.core.persistence.MovieRepository;
import com.alamobot.core.persistence.SeatRepository;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AlertService {
    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private SeatService seatService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private PaymentService paymentService;

    private ClosestSeatToScreenComparator closestSeatToScreenComparator = new ClosestSeatToScreenComparator();
    private EarliestShowtimeComparator earliestShowtimeComparator = new EarliestShowtimeComparator();

    public List<FilmAlertEntity> getAllFilmAlerts() {
        return alertRepository.findAll();
    }

    public FilmAlertEntity createFilmAlert(String filmName,
                                           Set<String> preferredCinemas,
                                           LocalTime earliestShowtime,
                                           LocalTime latestShowtime,
                                           Set<DayOfWeek> preferredDaysOfTheWeek,
                                           Boolean overrideSeatingAlgorithm,
                                           Integer seatCount) {
        if(!cinemaService.allCinemasExist(preferredCinemas)) {
            return null;
        }
        FilmAlertEntity filmAlertEntity = alertRepository.findByFilmNameContainingIgnoreCase(filmName);
        if(filmAlertEntity != null) {
            filmAlertEntity.setPreferredCinemas(preferredCinemas);
            filmAlertEntity.setEarliestShowtime(earliestShowtime);
            filmAlertEntity.setLatestShowtime(latestShowtime);
            filmAlertEntity.setPreferredDaysOfTheWeek(preferredDaysOfTheWeek);
            filmAlertEntity.setOverrideSeatingAlgorithm(overrideSeatingAlgorithm);
            filmAlertEntity.setSeatCount(seatCount);
        } else {
            filmAlertEntity = FilmAlertEntity.builder()
                    .filmName(filmName)
                    .overrideSeatingAlgorithm(overrideSeatingAlgorithm)
                    .preferredCinemas(preferredCinemas)
                    .earliestShowtime(earliestShowtime)
                    .latestShowtime(latestShowtime)
                    .preferredDaysOfTheWeek(preferredDaysOfTheWeek)
                    .seatCount(seatCount)
                    .build();
        }
        return alertRepository.save(filmAlertEntity);
    }

    public FilmAlertEntity updateFilmAlert(Integer alertId, String filmName, Boolean overrideSeatingAlgorithm, Integer seatCount) {
        Optional<FilmAlertEntity> filmAlertEntityOptional = alertRepository.findById(alertId);
        if(!filmAlertEntityOptional.isPresent()) {
            return null;
        }
        FilmAlertEntity filmAlertEntity = filmAlertEntityOptional.get();
        if(filmName != null) {
            filmAlertEntity.setFilmName(filmName);
        }
        if(overrideSeatingAlgorithm != null) {
            filmAlertEntity.setOverrideSeatingAlgorithm(overrideSeatingAlgorithm);
        }
        if(seatCount != null) {
            filmAlertEntity.setSeatCount(seatCount);
        }
        return alertRepository.save(filmAlertEntity);
    }

    public void deleteFilmAlert(Integer alertId) {
        alertRepository.deleteById(alertId);
    }

    void checkAlertsAndBuyTickets() {
        List<FilmAlertEntity> filmAlerts =  alertRepository.findAll();
        for(FilmAlertEntity currentFilmAlert: filmAlerts) {
            FilmEntity alertedFilm = getValidFilmFromSearchTerm(currentFilmAlert);
            if(alertedFilm == null) {
                continue;
            }
            List<MovieEntity> validMovieEntityList = getAllMoviesMatchingAlertTimingRequirements(alertedFilm.getId(), currentFilmAlert);
            if(validMovieEntityList.isEmpty()) {
                continue;
            }
            buyAsManyTicketsAsPossibleUpToAlertAmount(validMovieEntityList, currentFilmAlert);
            alertRepository.deleteById(currentFilmAlert.getId());
        }
    }

    private void buyAsManyTicketsAsPossibleUpToAlertAmount(List<MovieEntity> validMovieEntityList, FilmAlertEntity filmAlert) {
        boolean ticketsBoughtForShowing = false;
        for(MovieEntity movieEntity: validMovieEntityList) {
            int sessionId = movieEntity.getSessionId();
            int seatsLeftToBuy = filmAlert.getSeatCount();
            while(seatsLeftToBuy > 0) {
                if(movieEntity.getSeatsLeft() > filmAlert.getSeatCount()) {
                    List<SeatEntity> seatEntityList = getValidSeatsFromServerFromRow3AndBack(movieEntity);
                    List<Seat> seatsToBuy = getSeatBatchToBuy(filmAlert, seatEntityList);
                    boolean seatsBought = paymentService.buySeats(sessionId, seatsToBuy);
                    if(seatsBought) {
                        seatsLeftToBuy -= seatsToBuy.size();
                        ticketsBoughtForShowing = true;
                    }
                } else {
                    break;
                }
            }
            if (ticketsBoughtForShowing) {
                return;
            }
        }
    }

    private List<SeatEntity> getValidSeatsFromServerFromRow3AndBack(MovieEntity showtime) {
        seatService.getSeatsFromServerAndPersist(showtime);
        List<SeatEntity> seatEntityList = seatRepository.findAllBySessionIdAndSeatStatusAndRowIndexGreaterThanEqual(showtime.getSessionId(), "EMPTY", 2);
        seatEntityList.sort(closestSeatToScreenComparator);
        return seatEntityList;
    }

    @SneakyThrows
    private List<Seat> getSeatBatchToBuy(FilmAlertEntity filmAlert, List<SeatEntity> seatEntityList) {
        ArrayList<Seat> seatsToBuy = new ArrayList<>();
        for(SeatEntity seatEntity: seatEntityList) {
            if(seatsToBuy.size() < filmAlert.getSeatCount()) {
                Seat seat = new Seat();
                BeanUtils.copyProperties(seat, seatEntity);
                seatsToBuy.add(seat);
            }
            if(seatsToBuy.size() >= 10 || seatsToBuy.size() >= filmAlert.getSeatCount()) {
                break;
            }
        }
        return seatsToBuy;
    }

    private List<MovieEntity> getAllMoviesMatchingAlertTimingRequirements(
            String alertedFilmId,
            FilmAlertEntity filmAlert) {
        List<MovieEntity> filmShowtimes = movieRepository.findAllByFilmId(alertedFilmId);
        List<MovieEntity> validShowtimes = new ArrayList<>();
        MovieEntity earliestShowtime = MovieEntity.builder().sessionDateTime(LocalDateTime.MAX).build();
        for(MovieEntity currentShowtime: filmShowtimes) {
            if(currentShowtimeIsBeforeEarliestShowtime(earliestShowtime, currentShowtime)) {
                earliestShowtime = currentShowtime;
            }
            if(currentShowtimeMatchesAlert(earliestShowtime, currentShowtime, filmAlert)) {
                validShowtimes.add(currentShowtime);
            }
        }
        validShowtimes.sort(earliestShowtimeComparator);
        return validShowtimes;
    }

    private boolean currentShowtimeIsBeforeEarliestShowtime(MovieEntity earliestShowtime, MovieEntity currentShowtime) {
        return earliestShowtime.getSessionDateTime().isAfter(currentShowtime.getSessionDateTime());
    }

    private boolean currentShowtimeMatchesAlert(MovieEntity earliestShowtime, MovieEntity currentShowtime, FilmAlertEntity filmAlert) {
        return currentShowtimeWithinAWeekOfEarliestShowtime(earliestShowtime, currentShowtime) &&
                currentShowtimeBetweenAlertTimes(currentShowtime, filmAlert) &&
                currentShowtimeOnPreferredDayOfWeek(currentShowtime, filmAlert) &&
                currentShowtimeAtPreferredCinema(currentShowtime, filmAlert);
    }

    private boolean currentShowtimeWithinAWeekOfEarliestShowtime(MovieEntity earliestShowtime, MovieEntity currentShowtime) {
        return currentShowtime.getSessionDateTime().isBefore(earliestShowtime.getSessionDateTime().plusWeeks(1));
    }

    private boolean currentShowtimeBetweenAlertTimes(MovieEntity currentShowtime, FilmAlertEntity filmAlert) {
        return currentShowtime.getSessionDateTime().toLocalTime().isAfter(filmAlert.getEarliestShowtime()) &&
                currentShowtime.getSessionDateTime().toLocalTime().isBefore(filmAlert.getLatestShowtime());
    }

    private boolean currentShowtimeOnPreferredDayOfWeek(MovieEntity currentShowtime, FilmAlertEntity filmAlert) {
        return filmAlert.getPreferredDaysOfTheWeek().contains(currentShowtime.getSessionDateTime().toLocalDate().getDayOfWeek());
    }

    private boolean currentShowtimeAtPreferredCinema(MovieEntity currentShowtime, FilmAlertEntity filmAlert) {
        return filmAlert.getPreferredCinemas().contains(currentShowtime.getCinemaId());
    }

    private FilmEntity getValidFilmFromSearchTerm(FilmAlertEntity filmAlertEntity) {
        FilmEntity alertedFilm = null;
        List<FilmEntity> filmsMatchingAlertSearchTerm = filmRepository.findAllByNameContainingIgnoreCase(filmAlertEntity.getFilmName(), PageRequest.of(0, 50)).getContent();
        if(filmsMatchingAlertSearchTerm.isEmpty()) {
            return null;
        }
        for(FilmEntity currentFilm: filmsMatchingAlertSearchTerm) {
            if(isCurrentFilm2DAndFirstFilmOrShorterCurrentAlertedFilm(currentFilm, alertedFilm)) {
                alertedFilm = currentFilm;
            }
        }
        verifyAndMarkFilmAsWatched(alertedFilm);
        return alertedFilm;
    }

    private void verifyAndMarkFilmAsWatched(FilmEntity currentFilm) {
        if(currentFilm == null) {
            return;
        }
        currentFilm.setWatched(true);
        filmRepository.save(currentFilm);
    }

    private boolean isCurrentFilm2DAndFirstFilmOrShorterCurrentAlertedFilm(FilmEntity currentFilm, FilmEntity alertedFilm) {
        return !(currentFilm.getName().toUpperCase().contains("3D")) &&
                ((alertedFilm == null) ||
                (alertedFilm.getName().length() > currentFilm.getName().length()));
    }

    private class ClosestSeatToScreenComparator implements Comparator<SeatEntity> {
        @Override
        public int compare(SeatEntity seatEntity1, SeatEntity seatEntity2) {
            if(seatEntity1.getRowIndex() < seatEntity2.getRowIndex()) {
                return 1;
            } else if(seatEntity1.getRowIndex() > seatEntity2.getRowIndex()) {
                return -1;
            } else {
                if(seatEntity1.getColumnIndex() < seatEntity2.getColumnIndex()) {
                    return 1;
                } else if(seatEntity1.getColumnIndex() > seatEntity2.getColumnIndex()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    private class EarliestShowtimeComparator implements Comparator<MovieEntity> {
        @Override
        public int compare(MovieEntity movieEntity1, MovieEntity movieEntity2) {
            if(movieEntity1.getSessionDateTime().isBefore(movieEntity2.getSessionDateTime())) {
                return -11;
            } else if(movieEntity1.getSessionDateTime().isAfter(movieEntity2.getSessionDateTime())) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}