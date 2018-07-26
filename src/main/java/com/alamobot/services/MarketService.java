package com.alamobot.services;

import com.alamobot.core.AlamoUrls;
import com.alamobot.core.api.market.MarketDataContainer;
import com.alamobot.core.domain.MarketEntity;
import com.alamobot.core.persistence.MarketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MarketService {
    @Autowired
    MarketRepository marketRepository;

    private MarketEntityMapper marketEntityMapper = new MarketEntityMapper();
    private RestTemplate restTemplate = initRestTemplate();
    private HttpEntity<String> headersEntity = initHttpHeaders();

    public void markMarketAsViewed(String marketId) {
        Optional<MarketEntity> marketEntityOptional = marketRepository.findById(marketId);
        if(!marketEntityOptional.isPresent()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        MarketEntity marketEntity = marketEntityOptional.get();
        marketEntity.setWatched(true);
        marketRepository.save(marketEntity);
    }

    void getMarketListFromServerAndPersist() {
        List<MarketEntity> marketList = setWatchedMarkets(getMarketListFromServer());
        marketRepository.saveAll(marketList);
    }

    private List<MarketEntity> setWatchedMarkets(List<MarketEntity> marketList) {
        return marketList.stream()
                .map(marketEntity -> setWatchedOnMarketEntity(marketEntity))
                .collect(Collectors.toList());
    }

    private MarketEntity setWatchedOnMarketEntity(MarketEntity marketEntity) {
        Optional<MarketEntity> marketEntityOptional = marketRepository.findById(marketEntity.getId());
        boolean watchedStatus = marketEntityOptional.isPresent() ? marketEntityOptional.get().getWatched() : false;
        marketEntity.setWatched(watchedStatus);
        return marketEntity;
    }

    private List<MarketEntity> getMarketListFromServer() {
        String getMarketDetailsUrl = AlamoUrls.MARKET_BASE_URL;

        ResponseEntity<MarketDataContainer> marketDataContainerResponse =
                restTemplate.exchange(
                        getMarketDetailsUrl,
                        HttpMethod.GET,
                        headersEntity,
                        MarketDataContainer.class
                );
        MarketDataContainer marketDataContainer = marketDataContainerResponse.getBody();
        return marketEntityMapper.marketDataToMarketEntityList(marketDataContainer);
    }

    private RestTemplate initRestTemplate() {
        return new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    }

    //Just to hack the servers to let them think I'm coming from Chrome
    private HttpEntity<String> initHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "chrome");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>("parameters", headers);
    }
}
