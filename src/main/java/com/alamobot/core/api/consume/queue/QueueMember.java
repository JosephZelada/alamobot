package com.alamobot.core.api.consume.queue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueueMember {
    private Queue queueInfo;
    private String queueMemberId;
    private QueueMemberStatus queueMemberStatus;
    private Integer position;
    private Integer pollIntervalSeconds;
}
