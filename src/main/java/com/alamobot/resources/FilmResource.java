package com.alamobot.resources;

import com.alamobot.core.ResourcePaths;
import com.alamobot.services.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ResourcePaths.FILM_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class FilmResource {
    @Autowired
    FilmService filmService;

    @PostMapping("/{film_id}")
    public void markCinemaAsViewed(@PathVariable("film_id") String filmId) {
        filmService.markFilmAsViewed(filmId);
    }
}
