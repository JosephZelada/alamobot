package com.alamobot.services;

import com.alamobot.core.persistence.EntityPagingAndSortingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableSearchableService {
    public Page getAllEntities(String marketName, String sortBy, String orderBy, Integer pageNumber, Integer pageSize, EntityPagingAndSortingRepository repository) {
        SearchCriteria criteria = buildSearchCriteria(marketName, sortBy, orderBy, pageNumber, pageSize);
        Pageable pageable = PageRequest.of(criteria.getPageNumber(), criteria.getPageSize(), criteria.getSort());
        return repository.findAllByNameContainingIgnoreCase(criteria.getName(), pageable);
    }

    public SearchCriteria buildSearchCriteria(String name, String sortBy, String orderBy, Integer pageNumber, Integer pageSize) {
        String sortByColumn = sortBy == null || sortBy.equals("") ? "watched": sortBy;
        String orderByColumn = orderBy == null || !(orderBy.toUpperCase().equals("DESC") || orderBy.toUpperCase().equals("ASC")) ? "DESC" : orderBy.toUpperCase();
        Sort sort = new Sort(Sort.Direction.fromString(orderByColumn), sortByColumn);
        String searchByName = name == null ? "" : name;
        int pageNumberInt = pageNumber == null ? 0 : pageNumber - 1;
        int pageSizeInt = pageSize == null ? 10 : pageSize;
        return new SearchCriteria(sort, searchByName, pageNumberInt, pageSizeInt);
    }
}
