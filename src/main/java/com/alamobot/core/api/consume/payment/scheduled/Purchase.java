package com.alamobot.core.api.consume.payment.scheduled;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@JsonIgnoreProperties
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Purchase {
    private String filmSlug;
    private String cinemaId;
    private String bookingId;
    private String filmHoCode;
    private String marketId;
    private boolean isRefunded;
    private LocalDateTime sessionDateTimeClt;
}
