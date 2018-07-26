package com.alamobot.resources;

import com.alamobot.core.ResourcePaths;
import com.alamobot.services.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ResourcePaths.MARKET_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class MarketResource {
    @Autowired
    MarketService marketService;

    @PostMapping("/{market_id}")
    public void markMarketAsViewed(@PathVariable("market_id") String market_id) {
        marketService.markMarketAsViewed(market_id);
    }
}
