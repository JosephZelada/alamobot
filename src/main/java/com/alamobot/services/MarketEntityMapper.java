package com.alamobot.services;

import com.alamobot.core.api.market.MarketData;
import com.alamobot.core.api.market.MarketDataContainer;
import com.alamobot.core.domain.MarketEntity;

import java.util.List;
import java.util.stream.Collectors;

public class MarketEntityMapper {
    public List<MarketEntity> marketDataToMarketEntityList(MarketDataContainer marketDataContainer) {
        MarketData marketData = marketDataContainer.getData();
        return marketData.getMarketSummaries().stream()
                .map(marketSummary -> MarketEntity.builder()
                        .id(marketSummary.getId())
                        .name(marketSummary.getName())
                        .slug(marketSummary.getSlug())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
