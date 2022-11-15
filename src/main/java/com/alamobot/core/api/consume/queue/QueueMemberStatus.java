package com.alamobot.core.api.consume.queue;

public enum QueueMemberStatus {
    REGISTERED, // Member in queue
    ACTIVE, // Member is at the front of queue, eligible to get queueToken
    UNREGISTERED
}
