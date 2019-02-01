package com.alamobot.resources;

import com.alamobot.core.ResourcePaths;
import com.alamobot.core.domain.FilmWithSeatCount;
import com.alamobot.services.FilmService;
import com.alamobot.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

//TODO: Can CrossOrigin be called on the class vs the method?
//TODO: Can you get rid of CrossOrigin by doing http://localhost
@RestController
@RequestMapping(path = ResourcePaths.ADMIN_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminResource {

    @Autowired
    FilmService filmService;

    @Autowired
    SeatService seatService;

    @CrossOrigin
    @GetMapping("/filmsBought")
    public Collection<FilmWithSeatCount> getAllFilmsBought() {
        return filmService.getAllFilmsWithBoughtSeats();
    }

    @CrossOrigin
    @GetMapping("/takeSeat/{session_id}/{seat_id}")
    public void takeSeatForSession(@PathVariable("seat_id") int seatId,
                                   @PathVariable("name_person") String namePerson) {
        seatService.takeSeatForSession(seatId, namePerson);
    }
}
