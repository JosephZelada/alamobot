package com.alamobot.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
    private Sort sort;
    private String name;
    private int pageNumber;
    private int pageSize;
}
