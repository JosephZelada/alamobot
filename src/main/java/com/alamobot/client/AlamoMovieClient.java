package com.alamobot.client;

import com.alamobot.core.AlamoUrls;
import com.alamobot.core.api.consume.showtime.Market;
import com.alamobot.core.api.consume.showtime.MarketContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Slf4j
public class AlamoMovieClient {
    @Autowired
    private RestTemplate simpleRestTemplate;

    public MarketContainer getMovieListFromServer(String marketId) {
        String getMarketDetailsUrl = AlamoUrls.SHOWTIMES_FOR_MARKET_BASE_URL + marketId + "/";

        //TODO: Maybe swap restTemplate out with Feign?
        ResponseEntity<MarketContainer> marketContainerResponse =
                simpleRestTemplate.exchange(
                        getMarketDetailsUrl,
                        HttpMethod.GET,
                        null,
                        MarketContainer.class
                );
        MarketContainer marketContainer = marketContainerResponse.getBody();
        if(marketContainer == null) {
            log.info("No data received for market with ID " + marketId +" skipping");
            return MarketContainer.builder()
                    .Market(Market.builder()
                            .Dates(new ArrayList<>())
                            .MarketId(marketId)
                            .build())
                    .build();
        }
        return marketContainer;
    }
}
