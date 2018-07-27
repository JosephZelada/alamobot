package com.alamobot.services;

import com.alamobot.core.AlamoUrls;
import com.alamobot.core.api.Seat;
import com.alamobot.core.api.SeatMap;
import com.alamobot.core.api.consume.seats.DataContainer;
import com.alamobot.core.domain.FilmEntity;
import com.alamobot.core.domain.MovieEntity;
import com.alamobot.core.domain.SeatEntity;
import com.alamobot.core.persistence.FilmRepository;
import com.alamobot.core.persistence.MovieRepository;
import com.alamobot.core.persistence.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private FilmRepository filmRepository;

    private SeatEntityMapper seatEntityMapper = new SeatEntityMapper();
    private RestTemplate restTemplate = initRestTemplate();
    private HttpEntity<String> headersEntity = initHttpHeaders();

    public void getSeatsFromServerAndPersist(MovieEntity movieEntity) {
        List<SeatEntity> seatEntities = getSeatsFromServerForMovie(movieEntity);
        persistSeatEntities(seatEntities);
    }

    public SeatMap getSeatsForSessionId(int sessionId) {
        Optional<MovieEntity> movieEntityOptional = movieRepository.findById(sessionId);
        if(!movieEntityOptional.isPresent()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        MovieEntity movieEntity = movieEntityOptional.get();
        Optional<FilmEntity> filmEntityOptional = filmRepository.findById(movieEntity.getFilmId());
        if(!filmEntityOptional.isPresent()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        FilmEntity filmEntity = filmEntityOptional.get();
        List<SeatEntity> seatEntityList = seatRepository.findAllBySessionId(sessionId);

        SeatMap seatMap = SeatMap.builder().filmName(filmEntity.getName()).seats(new HashMap<>()).build();
        int maxRowIndex = 0;
        int maxColumnIndex = 0;
        Map<Integer, Map<Integer, Seat>> seats = seatMap.getSeats();
        for(SeatEntity seatEntity: seatEntityList) {
            if(!seats.containsKey(seatEntity.getRowIndex())) {
                seats.put(seatEntity.getRowIndex(),new HashMap<>());
            }
            maxRowIndex = maxRowIndex < seatEntity.getRowIndex() ? seatEntity.getRowIndex() : maxRowIndex;
            maxColumnIndex = maxColumnIndex < seatEntity.getColumnIndex() ? seatEntity.getColumnIndex() : maxColumnIndex;
            seats.get(seatEntity.getRowIndex())
                    .put(seatEntity.getColumnIndex(), Seat.builder()
                            .columnIndex(seatEntity.getColumnIndex())
                            .rowIndex(seatEntity.getRowIndex())
                            .seatNumber(seatEntity.getSeatNumber())
                            .rowNumber(seatEntity.getRowNumber())
                            .build()
                    );
        }

        List<Integer> keyList = new ArrayList<>(seats.keySet());
        Collections.sort(keyList);
        for(int i = 0; i < keyList.size() - 1; i++) {
            for(int n = keyList.get(i); n < keyList.get(i+1)-1; n++) {
                if(seats.containsKey(n)) {
                    throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                seats.put(n, new HashMap<>());
            }
        }
        seatMap.setSeats(seats);

        return seatMap;
    }

    private RestTemplate initRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        return restTemplate;
    }

    //Just to hack the servers to let them think I'm coming from Chrome
    private HttpEntity<String> initHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "chrome");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>("parameters", headers);
    }

    private List<SeatEntity> getSeatsFromServerForMovie(MovieEntity movieEntity) {
        String getSeatDetailsUrl = AlamoUrls.SEAT_CHART_BASE_URL + movieEntity.getCinemaId() + "/" + movieEntity.getSessionId();
        ResponseEntity<DataContainer> dataContainerResponse;
        try {
            dataContainerResponse = restTemplate.exchange(
                    getSeatDetailsUrl,
                    HttpMethod.GET,
                    headersEntity,
                    DataContainer.class
            );
        } catch (Exception e) {
            //TODO: Log exception if it ever happens
            return new ArrayList<>();
        }

        DataContainer dataContainer = dataContainerResponse.getBody();
        return seatEntityMapper.dataToSeatEntity(dataContainer, movieEntity.getSessionId());
    }

    private void persistSeatEntities(List<SeatEntity> seatEntities) {
        for(SeatEntity seatEntity: seatEntities) {
            seatRepository.save(seatEntity);
        }
    }

    private void setSeatToTaken(SeatEntity seatEntity) {
        //TODO: Must add fields to seatEntity, make sure not to override them. Do like you did with the movieEntity.watched query
    }

    private void setSeatToPaid(SeatEntity seatEntity) {
        //TODO: Must add fields to seatEntity, make sure not to override them. Do like you did with the movieEntity.watched query
    }
}
