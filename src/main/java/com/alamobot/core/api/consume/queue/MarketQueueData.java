package com.alamobot.core.api.consume.queue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketQueueData {
    private List<Queue> queues;
}
