package com.alamobot.resources;

import com.alamobot.core.ResourcePaths;
import com.alamobot.core.api.SeatMap;
import com.alamobot.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ResourcePaths.SEAT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class SeatResource {
    @Autowired
    SeatService seatService;

    @CrossOrigin
    @GetMapping("/{session_id}")
    public SeatMap getSeatsForSessionId(@PathVariable("session_id") int sessionId) {
        return seatService.getSeatsForSessionId(sessionId);
    }
}
