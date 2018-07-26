package com.alamobot.resources;

import com.alamobot.core.ResourcePaths;
import com.alamobot.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ResourcePaths.MOVIE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class MovieResource {
    @Autowired
    MovieService movieService;

    @PostMapping("/{session_id}")
    public void markMovieAsViewed(@PathVariable("session_id") int sessionId) {
        movieService.markMovieAsViewed(sessionId);
    }
}
