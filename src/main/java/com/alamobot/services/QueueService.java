package com.alamobot.services;

import com.alamobot.core.AlamoUrls;
import com.alamobot.core.api.consume.queue.MarketQueueDataContainer;
import com.alamobot.core.api.consume.queue.Queue;
import com.alamobot.core.api.consume.queue.QueueMemberContainer;
import com.alamobot.core.api.consume.queue.QueueMemberStatus;
import com.alamobot.core.api.consume.queue.QueueStatus;
import com.alamobot.core.api.consume.queue.QueueTokenDataContainer;
import com.alamobot.core.domain.FilmAlertEntity;
import com.alamobot.core.domain.MovieEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class QueueService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpEntity<String> headersEntity;

    public QueueAuthorization queueForTicket(MovieEntity movieEntity) {
        if(queuesExistForMovie(movieEntity)) {
            String queueId = getQueueIdAndWaitUntilProcessing(movieEntity);
            String queueMemberId = getInQueue(queueId);
            waitUntilQueueMemberIsAtFrontOfQueue(queueMemberId);
            String queueToken = getQueueToken(queueMemberId);
            return QueueAuthorization.builder()
                    .queueId(queueId)
                    .queueMemberId(queueMemberId)
                    .queueToken(queueToken)
                    .build();
        }
        return new QueueAuthorization();
    }

    private boolean queuesExistForMovie(MovieEntity movieEntity) {
        String getMarketQueueDetailsUrl = AlamoUrls.MARKET_WITH_QUEUES_BASE_URL + movieEntity.getCinemaId() + "/" + movieEntity.getSessionId();
        ResponseEntity<MarketQueueDataContainer> marketQueueDataContainerResponse =
                restTemplate.exchange(
                        getMarketQueueDetailsUrl,
                        HttpMethod.GET,
                        headersEntity,
                        MarketQueueDataContainer.class
                );
        MarketQueueDataContainer marketQueueDataContainer = marketQueueDataContainerResponse.getBody();
        long validMarketQueueCount = marketQueueDataContainer.getData().getQueues()
                .stream()
                .filter((queue) -> queue.getMarketId().equals(movieEntity.getMarketId()))
                .count();
        return validMarketQueueCount > 0;
    }

    private String getQueueIdAndWaitUntilProcessing(MovieEntity movieEntity) {
        String getMarketQueueDetailsUrl = AlamoUrls.MARKET_WITH_QUEUES_BASE_URL + movieEntity.getCinemaId() + "/" + movieEntity.getSessionId();
        QueueStatus queueStatus = QueueStatus.DORMANT;
        Queue marketQueue = Queue.builder().queueId("").build();
        while(queueStatus != QueueStatus.PROCESSING) {
            ResponseEntity<MarketQueueDataContainer> marketQueueDataContainerResponse =
                    restTemplate.exchange(
                            getMarketQueueDetailsUrl,
                            HttpMethod.GET,
                            headersEntity,
                            MarketQueueDataContainer.class
                    );
            MarketQueueDataContainer marketQueueDataContainer = marketQueueDataContainerResponse.getBody();
            marketQueue = marketQueueDataContainer.getData().getQueues()
                    .stream()
                    .filter((queue) -> queue.getMarketId().equals(movieEntity.getMarketId()))
                    .findFirst()
                    .get();
            queueStatus = marketQueue.getQueueStatus();
            // wait 10 seconds here before processing again
        }
        return marketQueue.getQueueId();
    }

    private String getInQueue(String queueId) {
        String queueRegistrationUrl = AlamoUrls.QUEUE_REGISTRATION_URL + queueId + "/register";
        ResponseEntity<QueueMemberContainer> queueMemberContainerResponse =
                restTemplate.exchange(
                        queueRegistrationUrl,
                        HttpMethod.POST,
                        headersEntity,
                        QueueMemberContainer.class
                );
        QueueMemberContainer queueMemberContainer = queueMemberContainerResponse.getBody();
        return queueMemberContainer.getData().getQueueMemberId();
    }

    private void waitUntilQueueMemberIsAtFrontOfQueue(String queueMemberId) {
        String queueTokenUrl = AlamoUrls.QUEUE_TOKEN_URL + queueMemberId;
        QueueMemberStatus queueMemberStatus = QueueMemberStatus.REGISTERED;
        while(queueMemberStatus != QueueMemberStatus.ACTIVE) {
            ResponseEntity<QueueMemberContainer> queueMemberContainerResponse =
                    restTemplate.exchange(
                            queueTokenUrl,
                            HttpMethod.GET,
                            headersEntity,
                            QueueMemberContainer.class
                    );
            QueueMemberContainer queueMemberContainer = queueMemberContainerResponse.getBody();
            queueMemberStatus = queueMemberContainer.getData().getQueueMemberStatus();
            // wait queueMemberContainer.getData().getPollIntervalSeconds() seconds here before processing again
        }
    }

    private String getQueueToken(String queueMemberId) {
        String queueTokenUrl = AlamoUrls.QUEUE_TOKEN_URL + queueMemberId + "/token";
        ResponseEntity<QueueTokenDataContainer> queueTokenDataContainerResponse =
                restTemplate.exchange(
                        queueTokenUrl,
                        HttpMethod.GET,
                        headersEntity,
                        QueueTokenDataContainer.class
                );
        QueueTokenDataContainer queueTokenDataContainer = queueTokenDataContainerResponse.getBody();
        return queueTokenDataContainer.getData().getQueueToken();
    }
}
