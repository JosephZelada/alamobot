package com.alamobot.resources;

import com.alamobot.core.ResourcePaths;
import com.alamobot.core.domain.CinemaEntity;
import com.alamobot.services.CinemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = ResourcePaths.CINEMA_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class CinemaResource {
    @Autowired
    CinemaService cinemaService;

    @CrossOrigin
    @PostMapping("/{cinema_id}")
    public void markCinemaAsViewed(@PathVariable("cinema_id") String cinemaId, @RequestParam("watched") Boolean watched) {
        cinemaService.markCinemaAsViewed(cinemaId, watched);
    }

    @CrossOrigin
    @GetMapping
    public List<CinemaEntity> getAllWatchedCinemasInMarket(@RequestParam("market_id") String marketId) {
        return cinemaService.getAllWatchedCinemasForMarket(marketId);
    }
}
