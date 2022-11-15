package com.alamobot.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueueAuthorization {
    private String queueId;
    private String queueMemberId;
    private String queueToken;

    public boolean isQueueAuthorizationPopulated() {
        return !StringUtils.isEmpty(queueId) &&
                !StringUtils.isEmpty(queueMemberId) &&
                !StringUtils.isEmpty(queueToken);
    }
}
