package com.alamobot.resources;

import com.alamobot.core.ResourcePaths;
import com.alamobot.core.domain.FilmAlertEntity;
import com.alamobot.services.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

//TODO: Can CrossOrigin be called on the class vs the method?
//TODO: Can you get rid of CrossOrigin by doing http://localhost
@RestController
@RequestMapping(path = ResourcePaths.ALERT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AlertResource {

    @Autowired
    AlertService alertService;

    @CrossOrigin
    @GetMapping
    public List<FilmAlertEntity> getAllFilmsAlerts() {

        return alertService.getAllFilmAlerts();
    }

    @CrossOrigin
    @PutMapping
    public FilmAlertEntity createFilmAlert(@RequestParam("film_name") String filmName,
                                           @RequestParam("preferred_cinemas") Set<String> preferredCinemas,
                                           @RequestParam("earliest_showtime") String earliestShowtimeString,
                                           @RequestParam("latest_showtime") String latestShowtimeString,
                                           @RequestParam("preferred_days_of_the_week") Set<DayOfWeek> preferredDaysOfTheWeek,
                                           @RequestParam("override_seating_algorithm") Boolean overrideSeatingAlgorithm,
                                           @RequestParam("seat_count") Integer seatCount) {
        //Preferred cinemas, times, days of the week, and seating algorithm determined elsewhere across the board. Maybe make these parameters?
        //overrideSeatingAlgorithm determines if seats should be bought in the first two rows or not
        LocalTime earliestShowtime = LocalTime.parse(earliestShowtimeString);
        LocalTime latestShowtime = LocalTime.parse(latestShowtimeString);
        return alertService.createFilmAlert(filmName, preferredCinemas, earliestShowtime, latestShowtime, preferredDaysOfTheWeek, overrideSeatingAlgorithm, seatCount);
    }

    @CrossOrigin
    @PostMapping("/{alert_id}")
    public FilmAlertEntity updateFilmAlert(@PathVariable("alert_id") Integer alertId,
                                           @RequestParam("film_name") String filmName,
                                           @RequestParam("override_seating_algorithm") Boolean overrideSeatingAlgorithm,
                                           @RequestParam("seat_count") Integer seatCount) {
        return alertService.updateFilmAlert(alertId, filmName, overrideSeatingAlgorithm, seatCount);
    }

    @CrossOrigin
    @DeleteMapping("/{alert_id}")
    public void deleteFilmAlert(@PathVariable("alert_id") Integer alertId) {
        alertService.deleteFilmAlert(alertId);
    }
}
