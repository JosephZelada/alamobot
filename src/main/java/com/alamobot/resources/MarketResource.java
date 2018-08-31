package com.alamobot.resources;

import com.alamobot.core.ResourcePaths;
import com.alamobot.core.domain.MarketEntity;
import com.alamobot.services.MarketService;
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

@RestController
@RequestMapping(path = ResourcePaths.MARKET_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class MarketResource {
    private static final String SORT_BY = "sort_by";
    private static final String ORDER_BY = "order_by";
    private static final String MARKET_NAME = "market_name";
    private static final String PAGE_NUMBER = "page_number";
    private static final String PAGE_SIZE = "page_size";

    @Autowired
    MarketService marketService;

    @CrossOrigin
    @PostMapping("/{market_id}")
    public void markMarketAsViewed(@PathVariable("market_id") String market_id, @RequestParam("watched") Boolean watched) {
        marketService.markMarketAsViewed(market_id, watched);
    }

    @CrossOrigin
    @GetMapping
    public Page<MarketEntity> getAllMarkets(@RequestParam(value = SORT_BY, required = false) String sort_by,
                                            @RequestParam(value = ORDER_BY, required = false) String order_by,
                                            @RequestParam(value = MARKET_NAME, required = false) String market_name,
                                            @RequestParam(value = PAGE_NUMBER, required = false) Integer page_number,
                                            @RequestParam(value = PAGE_SIZE, required = false) Integer page_size) {
        return marketService.getAllMarkets(market_name, sort_by, order_by, page_number, page_size);
    }
}
