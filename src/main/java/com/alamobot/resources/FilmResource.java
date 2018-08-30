package com.alamobot.resources;

import com.alamobot.core.ResourcePaths;
import com.alamobot.core.api.FilmShowtimes;
import com.alamobot.core.domain.FilmEntity;
import com.alamobot.services.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = ResourcePaths.FILM_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class FilmResource {
    @Autowired
    FilmService filmService;

    @CrossOrigin
    @PostMapping("/{film_id}")
    public Boolean markFilmaAsViewed(@PathVariable("film_id") String filmId) {
        filmService.markFilmAsViewed(filmId);
        return true;
    }

    @CrossOrigin
    @GetMapping
    public List<FilmEntity> getAllMovies() {
        return filmService.getAllFilms();
    }

    @CrossOrigin
    @GetMapping("/{film_id}")
    public FilmShowtimes getAllMovieShowtimes(@PathVariable("film_id") String filmId) {
        return filmService.getAllFilmShowtimes(filmId);
    }
}
