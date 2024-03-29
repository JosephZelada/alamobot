package com.alamobot.services;

import com.alamobot.core.api.FilmAlert;
import com.alamobot.core.api.Seat;
import com.alamobot.core.api.consume.market.MarketDataContainer;
import com.alamobot.core.domain.FilmAlertEntity;
import com.alamobot.core.domain.FilmEntity;
import com.alamobot.core.domain.MarketEntity;
import com.alamobot.core.domain.MovieEntity;
import com.alamobot.core.domain.SeatEntity;
import com.alamobot.core.persistence.AlertRepository;
import com.alamobot.core.persistence.FilmRepository;
import com.alamobot.core.persistence.MarketRepository;
import com.alamobot.core.persistence.MovieRepository;
import com.alamobot.core.persistence.SeatRepository;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private MarketRepository marketRepository;

    @Autowired
    private SeatService seatService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private QueueService queueService;

    @Autowired
    private FilmAlertEntityApiMapper filmAlertEntityApiMapper;

    private final List<String> SEAT_STYLES_TO_EXCLUDE = Arrays.asList("HANDICAP", "COMPANION");

    private ClosestSeatToScreenComparator closestSeatToScreenComparator = new ClosestSeatToScreenComparator();
    private EarliestShowtimeComparator earliestShowtimeComparator = new EarliestShowtimeComparator();

    public List<FilmAlert> getAllFilmAlerts() {
        List<FilmAlertEntity> filmAlertEntityList = alertRepository.findAll();
        return filmAlertEntityList.stream().map(filmAlertEntity -> filmAlertEntityApiMapper.toApi(filmAlertEntity)).collect(Collectors.toList());
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
        FilmAlertEntity filmAlertEntity = FilmAlertEntity.builder()
                .filmName(filmName)
                .overrideSeatingAlgorithm(overrideSeatingAlgorithm)
                .preferredCinemas(preferredCinemas)
                .earliestShowtime(earliestShowtime)
                .latestShowtime(latestShowtime)
                .preferredDaysOfTheWeek(preferredDaysOfTheWeek)
                .seatCount(seatCount)
                .build();
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
            boolean ticketsBought = buyAsManyTicketsAsPossibleUpToAlertAmount(validMovieEntityList, currentFilmAlert);
            if(ticketsBought) {
                alertRepository.deleteById(currentFilmAlert.getId());
            }
        }
    }

    private boolean buyAsManyTicketsAsPossibleUpToAlertAmount(List<MovieEntity> validMovieEntityList, FilmAlertEntity filmAlert) {
        boolean ticketsBoughtForShowing = false;
        MarketEntity marketEntity = marketRepository.findById(validMovieEntityList.get(0).getMarketId()).get();
        for(MovieEntity movieEntity: validMovieEntityList) {
            int sessionId = movieEntity.getSessionId();
            int seatsLeftToBuy = filmAlert.getSeatCount();
            SeatEntity furthestSeatOwned = null;
            //Need to check with each iteration if the showtime still has seats for us to buy
            //Once you've started buying tickets in a showtime, don't stop until the seats are all gone or we have all the seats we want
            List<SeatEntity> seatEntityList;
            if(filmAlert.getOverrideSeatingAlgorithm()) {
                seatEntityList = getValidSeatsFromServerWithOverride(movieEntity, furthestSeatOwned);
            } else {
                seatEntityList = getValidSeatsFromServerFromRow3AndBack(movieEntity, furthestSeatOwned);
            }
            //Need to keep track of seats bought because of the mock
            Map<Integer, Seat> mockSeatsBought = new HashMap<>();
            while(shouldBuyMoreSeats(filmAlert, seatsLeftToBuy, seatEntityList, ticketsBoughtForShowing)) {
                List<Seat> seatsToBuy;
                if(paymentService.paymentStubActive) {
                    seatsToBuy = getMockSeatBatchToBuy(seatsLeftToBuy, seatEntityList, mockSeatsBought);
                } else {
                    seatsToBuy = getSeatBatchToBuy(seatsLeftToBuy, seatEntityList);
                }
                filmAlert.setSeatsQueuedFor(filmAlert.getSeatsQueuedFor() + seatsToBuy.size());
                alertRepository.save(filmAlert);
                QueueAuthorization queueAuthorization = queueService.queueForTicket(movieEntity);
                boolean seatsBought = paymentService.buySeats(sessionId, seatsToBuy, queueAuthorization, marketEntity.getSlug());
                if(seatsBought) {
                    seatsLeftToBuy -= seatsToBuy.size();
                    ticketsBoughtForShowing = true;
                    if(paymentService.paymentStubActive) {
                        for(Seat seat: seatsToBuy) {
                            mockSeatsBought.put(seat.getId(), seat);
                        }
                    }
                    filmAlert.setSeatCount(seatsLeftToBuy);
                    furthestSeatOwned = seatRepository.findById(seatsToBuy.get(seatsToBuy.size() - 1).getId()).get();
                } else {
                    filmAlert.setSeatsQueuedFor(filmAlert.getSeatsQueuedFor() - seatsToBuy.size());
                }
                alertRepository.save(filmAlert);
                //Test here how much faster ticket buys are without persisting seats until the end
                if(filmAlert.getOverrideSeatingAlgorithm()) {
                    seatEntityList = getValidSeatsFromServerWithOverride(movieEntity, furthestSeatOwned);
                } else {
                    seatEntityList = getValidSeatsFromServerFromRow3AndBack(movieEntity, furthestSeatOwned);
                }
            }
            if (ticketsBoughtForShowing) {
                break;
            }
        }
        return ticketsBoughtForShowing;
    }

    private boolean shouldBuyMoreSeats(FilmAlertEntity filmAlert, Integer seatsLeftToBuy, List<SeatEntity> seatEntityList, boolean ticketsBoughtForShowing) {
        return seatsQueuedForDoesNotExceedTotalSeatsNeeded(filmAlert) &&
                (
                        validSeatsLeftInTheaterAreGreaterThanAmountOfSeatsStillNeeded(seatsLeftToBuy, seatEntityList) ||
                                ticketsBoughtForShowing
                ) &&
                seatsLeftToBuy > 0 &&
                seatEntityList.size() != 0;
    }

    private boolean validSeatsLeftInTheaterAreGreaterThanAmountOfSeatsStillNeeded(Integer seatsLeftToBuy, List<SeatEntity> seatEntityList) {
        return seatEntityList.size() >= seatsLeftToBuy;
    }

    private boolean seatsQueuedForDoesNotExceedTotalSeatsNeeded(FilmAlertEntity filmAlert) {
        return filmAlert.getSeatsQueuedFor() < filmAlert.getSeatCount();
    }

    private List<SeatEntity> getValidSeatsFromServerFromRow3AndBack(MovieEntity showtime, SeatEntity furthestSeatOwned) {
        seatService.getSeatsFromServerAndPersist(showtime);
        List<SeatEntity> seatEntityList = seatRepository.findAllBySessionIdAndSeatStatusAndSeatBoughtAndRowIndexGreaterThanEqualAndSeatStyleNotIn(showtime.getSessionId(), "EMPTY", false, 2, SEAT_STYLES_TO_EXCLUDE);
        seatEntityList.sort(closestSeatToScreenComparator);
        return getSeatsInGroupsOfTwoOrMore(seatEntityList, furthestSeatOwned);
    }

    private List<SeatEntity> getValidSeatsFromServerWithOverride(MovieEntity showtime, SeatEntity furthestSeatOwned) {
        seatService.getSeatsFromServerAndPersist(showtime);
        List<SeatEntity> seatEntityList = seatRepository.findAllBySessionIdAndSeatStatusAndSeatBoughtAndSeatStyleNotIn(showtime.getSessionId(), "EMPTY", false, SEAT_STYLES_TO_EXCLUDE);
        seatEntityList.sort(closestSeatToScreenComparator);
        return getSeatsInGroupsOfTwoOrMore(seatEntityList, furthestSeatOwned);
    }

    private List<SeatEntity> getSeatsInGroupsOfTwoOrMore(List<SeatEntity> seatEntityList, SeatEntity furthestSeatOwned) {
        //Remove any seats that are in groups of 2 or less
        List<SeatEntity> seatStagingList = new ArrayList<>();
        List<SeatEntity> validSeatEntities = new ArrayList<>();
        for(SeatEntity currentSeat: seatEntityList) {
            if(seatStagingList.isEmpty()) {
                seatStagingList.add(currentSeat);
            } else {
                //Get tail of seatStagingList
                SeatEntity lastStagingSeatEntity = seatStagingList.get(seatStagingList.size() - 1);
                //Check if currentSeat is next to tail of seatStagingList in theater
                if(seatsAreNextToEachOther(currentSeat, lastStagingSeatEntity)) {
                    //If so, add currentSeat to end of seatStagingList
                    seatStagingList.add(currentSeat);
                } else {
                    if(seatStagingList.size() >= 2 || (seatStagingList.size() == 1 && seatsAreNextToEachOther(furthestSeatOwned, seatStagingList.get(0)))) {
                        //Else, check if seatStagingList is 3 or more items long. If so, add seatStagingList to validSeatEntities
                        validSeatEntities.addAll(seatStagingList);
                    }
                    //In all cases, clear out seatStagingList and add currentSeat to seatStagingList, because we are in a new group
                    seatStagingList.clear();
                    seatStagingList.add(currentSeat);
                }
            }
        }
        if(seatStagingList.size() >= 3) {
            //Else, check if seatStagingList is 3 or more items long. If so, add seatStagingList to validSeatEntities
            validSeatEntities.addAll(seatStagingList);
        }
        return validSeatEntities;
    }

    private boolean seatsAreNextToEachOther(SeatEntity currentSeat, SeatEntity previousSeat) {
        if(currentSeat == null || previousSeat == null) {
            return false;
        }
        return currentSeat.getRowIndex().equals(previousSeat.getRowIndex()) && Math.abs(currentSeat.getColumnIndex() - previousSeat.getColumnIndex()) == 1;
    }

    @SneakyThrows
    private List<Seat> getSeatBatchToBuy(int seatsLeftToBuy, List<SeatEntity> seatEntityList) {
        ArrayList<Seat> seatsToBuy = new ArrayList<>();
        for(SeatEntity seatEntity: seatEntityList) {
            if(seatsToBuy.size() < seatsLeftToBuy) {
                Seat seat = new Seat();
                BeanUtils.copyProperties(seat, seatEntity);
                seatsToBuy.add(seat);
            }
            if(seatsToBuy.size() >= 10 || seatsToBuy.size() >= seatsLeftToBuy) {
                break;
            }
        }
        return seatsToBuy;
    }

    @SneakyThrows
    private List<Seat> getMockSeatBatchToBuy(int seatsLeftToBuy, List<SeatEntity> seatEntityList, Map<Integer, Seat> mockSeatsBought) {
        ArrayList<Seat> seatsToBuy = new ArrayList<>();
        for(SeatEntity seatEntity: seatEntityList) {
            if(seatsToBuy.size() < seatsLeftToBuy && mockSeatsBought.get(seatEntity.getId()) == null) {
                Seat seat = new Seat();
                BeanUtils.copyProperties(seat, seatEntity);
                seatsToBuy.add(seat);
            }
            if(seatsToBuy.size() >= 10 || seatsToBuy.size() >= seatsLeftToBuy) {
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
                return -1;
            } else if(seatEntity1.getRowIndex() > seatEntity2.getRowIndex()) {
                return 1;
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
                return -1;
            } else if(movieEntity1.getSessionDateTime().isAfter(movieEntity2.getSessionDateTime())) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
