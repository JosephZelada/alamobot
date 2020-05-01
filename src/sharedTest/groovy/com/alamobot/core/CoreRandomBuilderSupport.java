package com.alamobot.core;

import com.alamobot.core.domain.RandomMovieEntityBuilder;
import com.alamobot.core.persistence.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class CoreRandomBuilderSupport {
    @Autowired
    MovieRepository movieRepository;

    public RandomMovieEntityBuilder movieEntity() {
        return new RandomMovieEntityBuilder(movieRepository);
    }
}
