package com.alamobot.resources;

import com.alamobot.core.ResourcePaths;
import com.alamobot.services.CinemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ResourcePaths.CINEMA_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class CinemaResource {
    @Autowired
    CinemaService cinemaService;

    @PostMapping("/{cinema_id}")
    public void markCinemaAsViewed(@PathVariable("cinema_id") String cinemaId) {
        cinemaService.markCinemaAsViewed(cinemaId);
    }
}
