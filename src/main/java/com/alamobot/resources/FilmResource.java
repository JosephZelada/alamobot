package com.alamobot.resources;

import com.alamobot.core.ResourcePaths;
import com.alamobot.core.api.FilmShowtimes;
import com.alamobot.core.domain.FilmEntity;
import com.alamobot.services.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//TODO: Can CrossOrigin be called on the class vs the method?
//TODO: Can you get rid of CrossOrigin by doing http://localhost
@RestController
@RequestMapping(path = ResourcePaths.FILM_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class FilmResource {
    @Autowired
    FilmService filmService;
    private static final String SORT_BY = "sort_by";
    private static final String ORDER_BY = "order_by";
    private static final String PAGE_NUMBER = "page_number";
    private static final String PAGE_SIZE = "page_size";
    private static final String SEARCH_TERM = "search_term";

    @CrossOrigin
    @PostMapping("/{film_id}")
    public Boolean markFilmAsViewed(@PathVariable("film_id") String filmId, @RequestParam("watched") Boolean watched) {
        filmService.markFilmAsViewed(filmId, watched);
        return true;
    }

    @CrossOrigin
    @GetMapping
    public Page<FilmEntity> getAllMovies(@RequestParam(value = SORT_BY, required = false) String sort_by,
                                         @RequestParam(value = ORDER_BY, required = false) String order_by,
                                         @RequestParam(value = SEARCH_TERM, required = false) String search_term,
                                         @RequestParam(value = PAGE_NUMBER, required = false) Integer page_number,
                                         @RequestParam(value = PAGE_SIZE, required = false) Integer page_size) {
        return filmService.getAllFilms(search_term, sort_by, order_by, page_number, page_size);
    }

    @CrossOrigin
    @GetMapping("/{film_id}")
    public FilmShowtimes getAllMovieShowtimes(@PathVariable("film_id") String filmId) {
        return filmService.getAllFilmShowtimes(filmId);
    }
}
