package com.alamobot.services;

import com.alamobot.core.AlamoUrls;
import com.alamobot.core.api.seats.DataContainer;
import com.alamobot.core.domain.MovieEntity;
import com.alamobot.core.domain.SeatEntity;
import com.alamobot.core.persistence.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    private SeatEntityMapper seatEntityMapper = new SeatEntityMapper();
    private RestTemplate restTemplate = initRestTemplate();

    HttpEntity<String> headersEntity = initHttpHeaders();

    public void getSeatsFromServerAndPersist(MovieEntity movieEntity) {
        List<SeatEntity> seatEntities = getSeatsFromServerForMovie(movieEntity);
        persistSeatEntities(seatEntities);
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
        //Must add fields to seatEntity, make sure not to override them. Do like you did with the movieEntity.watched query
    }

    private void setSeatToPaid(SeatEntity seatEntity) {
        //Must add fields to seatEntity, make sure not to override them. Do like you did with the movieEntity.watched query
    }
}
