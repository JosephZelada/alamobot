package com.alamobot.resources;

import com.alamobot.core.ResourcePaths;
import com.alamobot.core.domain.CinemaEntity;
import com.alamobot.core.domain.MarketEntity;
import com.alamobot.services.CinemaService;
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
    private static final String SEARCH_TERM = "search_term";
    private static final String PAGE_NUMBER = "page_number";
    private static final String PAGE_SIZE = "page_size";
    private static final String MARKET_ID = "market_id";

    @Autowired
    MarketService marketService;

    @Autowired
    CinemaService cinemaService;

    @CrossOrigin
    @PostMapping("/{market_id}")
    public void markMarketAsViewed(@PathVariable("market_id") String market_id, @RequestParam("watched") Boolean watched) {
        marketService.markMarketAsViewed(market_id, watched);
    }

    @CrossOrigin
    @GetMapping
    public Page<MarketEntity> getAllMarkets(@RequestParam(value = SORT_BY, required = false) String sort_by,
                                            @RequestParam(value = ORDER_BY, required = false) String order_by,
                                            @RequestParam(value = SEARCH_TERM, required = false) String search_term,
                                            @RequestParam(value = PAGE_NUMBER, required = false) Integer page_number,
                                            @RequestParam(value = PAGE_SIZE, required = false) Integer page_size) {
        return marketService.getAllMarkets(search_term, sort_by, order_by, page_number, page_size);
    }

    @CrossOrigin
    @GetMapping("/{market_id}/{film_id}")
    public Page<CinemaEntity> getAllCinemasForFilmInMarket(@RequestParam(value = SORT_BY, required = false) String sort_by,
                                                     @RequestParam(value = ORDER_BY, required = false) String order_by,
                                                     @RequestParam(value = SEARCH_TERM, required = false) String search_term,
                                                     @RequestParam(value = PAGE_NUMBER, required = false) Integer page_number,
                                                     @RequestParam(value = PAGE_SIZE, required = false) Integer page_size,
                                                     @PathVariable("market_id") String market_id,
                                                     @PathVariable("film_id") String filmId) {
        return cinemaService.getAllCinemasForFilmInMarket(sort_by, order_by, page_number, page_size, search_term, market_id, filmId);
    }
}
