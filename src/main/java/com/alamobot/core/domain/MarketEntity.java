package com.alamobot.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MarketEntity extends PagingAndSortingEntity {
    @Id
    private String id;
    private String name;
    private String slug;
    private Boolean watched;
}
