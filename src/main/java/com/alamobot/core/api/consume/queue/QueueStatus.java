package com.alamobot.core.api.consume.queue;

public enum QueueStatus {
    PROCESSING, // Queue is accepting requests
    DORMANT, // Queue is not active yet
    ACTIVE,
    QUEUING
}
