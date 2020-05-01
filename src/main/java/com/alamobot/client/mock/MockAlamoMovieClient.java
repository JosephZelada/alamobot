package com.alamobot.client.mock;

import com.alamobot.client.AlamoMovieClient;
import com.alamobot.core.api.consume.showtime.Market;
import com.alamobot.core.api.consume.showtime.MarketContainer;

import java.util.ArrayList;

public class MockAlamoMovieClient extends AlamoMovieClient {

    @Override
    public MarketContainer getMovieListFromServer(String marketId) {
        return MarketContainer.builder()
                .Market(Market.builder()
                        .Dates(new ArrayList<>())
                        .MarketId(marketId)
                        .build())
                .build();
    }
}
