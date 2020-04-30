package com.alamobot.services;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class HighWatermarkService {
    private Map<String, LocalDateTime> highWatermarkDateMap = new HashMap<>();

    public LocalDateTime updateHighWatermarkForMarket(String marketId) {
        return highWatermarkDateMap.putIfAbsent(marketId, LocalDateTime.MIN);
    }

    public LocalDateTime updateHighWatermarkForMarket(String marketId, LocalDateTime newHighWatermark) {
        return highWatermarkDateMap.putIfAbsent(marketId, LocalDateTime.MIN);
    }

    public LocalDateTime getHighWatermarkForMarket(String marketId) {
        return highWatermarkDateMap.get(marketId);
    }
}
