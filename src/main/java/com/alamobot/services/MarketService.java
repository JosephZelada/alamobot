package com.alamobot.services;

import com.alamobot.core.AlamoUrls;
import com.alamobot.core.api.consume.market.MarketDataContainer;
import com.alamobot.core.domain.MarketEntity;
import com.alamobot.core.persistence.MarketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MarketService {
    @Autowired
    MarketRepository marketRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpEntity<String> headersEntity;

    private MarketEntityMapper marketEntityMapper = new MarketEntityMapper();

    public void markMarketAsViewed(String marketId, Boolean watched) {
        Optional<MarketEntity> marketEntityOptional = marketRepository.findById(marketId);
        if(!marketEntityOptional.isPresent()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        MarketEntity marketEntity = marketEntityOptional.get();
        marketEntity.setWatched(watched);
        marketRepository.save(marketEntity);
    }

    public Page<MarketEntity> getAllMarkets(String marketName, String sortBy, String orderBy, Integer pageNumber, Integer pageSize) {
        SearchCriteria criteria = buildSearchCriteria(marketName, sortBy, orderBy, pageNumber, pageSize);
        Pageable pageable = PageRequest.of(criteria.getPageNumber(), criteria.getPageSize(), criteria.getSort());
        return marketRepository.findAllByNameContainingIgnoreCase(criteria.getName(), pageable);
    }

    private SearchCriteria buildSearchCriteria(String name, String sortBy, String orderBy, Integer pageNumber, Integer pageSize) {
        String sortByColumn = name == null || name.equals("") ? "watched": sortBy;
        String orderByColumn = orderBy == null || !(orderBy.toUpperCase().equals("DESC") || orderBy.toUpperCase().equals("ASC")) ? "DESC" : orderBy.toUpperCase();
        Sort sort = new Sort(Sort.Direction.fromString(orderByColumn), sortByColumn);
        String searchByName = name == null ? "" : name;
        int pageNumberInt = pageNumber == null ? 0 : pageNumber - 1;
        int pageSizeInt = pageSize == null ? 10 : pageSize;
        return new SearchCriteria(sort, searchByName, pageNumberInt, pageSizeInt);
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
}
