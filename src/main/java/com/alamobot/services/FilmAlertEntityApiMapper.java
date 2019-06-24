package com.alamobot.services;

import com.alamobot.core.api.FilmAlert;
import com.alamobot.core.domain.FilmAlertEntity;
import com.alamobot.core.persistence.CinemaRepository;
import com.google.common.base.CaseFormat;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class FilmAlertEntityApiMapper {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CinemaRepository cinemaRepository;

    public FilmAlertEntity toEntity(FilmAlert filmAlert) {
        FilmAlertEntity filmAlertEntity = new FilmAlertEntity();
        modelMapper.map(filmAlert, filmAlertEntity);
        mapCinemaFromNameToId(filmAlertEntity);
        mapShowtimesFromDateTimeToTime(filmAlertEntity, filmAlert);
        return filmAlertEntity;
    }

    public FilmAlert toApi(FilmAlertEntity filmAlertEntity) {
        FilmAlert filmAlert = new FilmAlert();
        modelMapper.map(filmAlertEntity, filmAlert);
        mapCinemaFromIdToName(filmAlert);
        mapDaysOfWeekFromEnumToCamelCase(filmAlert);
        mapShowtimesFromTimeToDateTime(filmAlertEntity, filmAlert);
        return filmAlert;
    }

    private void mapCinemaFromIdToName(FilmAlert filmAlert) {
        filmAlert.setPreferredCinemas(filmAlert.getPreferredCinemas()
                                              .stream()
                                              .map(cinemaId -> cinemaRepository.findById(cinemaId).get().getName())
                                              .collect(Collectors.toSet()));
    }

    private void mapCinemaFromNameToId(FilmAlertEntity filmAlertEntity) {
        filmAlertEntity.setPreferredCinemas(filmAlertEntity.getPreferredCinemas()
                                              .stream()
                                              .map(cinemaName -> cinemaRepository.findByName(cinemaName).get().getName())
                                              .collect(Collectors.toSet()));
    }

    private void mapDaysOfWeekFromEnumToCamelCase(FilmAlert filmAlert) {
        filmAlert.setPreferredDaysOfTheWeek(filmAlert.getPreferredDaysOfTheWeek()
                                              .stream()
                                              .map(dayOfWeek -> CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, dayOfWeek))
                                              .collect(Collectors.toSet()));
    }

    private void mapShowtimesFromTimeToDateTime(FilmAlertEntity filmAlertEntity, FilmAlert filmAlert) {
        filmAlert.setEarliestShowtime(
                LocalDateTime.of(LocalDate.of(1970, 1,1), filmAlertEntity.getEarliestShowtime())
        );
        filmAlert.setLatestShowtime(
                LocalDateTime.of(LocalDate.of(1970, 1,1), filmAlertEntity.getLatestShowtime())
        );
    }

    private void mapShowtimesFromDateTimeToTime(FilmAlertEntity filmAlertEntity, FilmAlert filmAlert) {
        filmAlertEntity.setEarliestShowtime(
                filmAlert.getEarliestShowtime().toLocalTime()
        );
        filmAlertEntity.setLatestShowtime(
                filmAlert.getLatestShowtime().toLocalTime()
        );
    }
}
