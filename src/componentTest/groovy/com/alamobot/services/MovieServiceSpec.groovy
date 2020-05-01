package com.alamobot.services

import com.alamobot.ComponentTest
import com.alamobot.core.domain.MovieEntity
import com.alamobot.core.persistence.MovieRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static com.alamobot.core.CoreARandom.aRandom

@ComponentTest
class MovieServiceSpec extends Specification {

    @Autowired
    MovieService movieService

    @Autowired
    MovieRepository movieRepository

    def "should mark showtime as watched"() {
        given:
        MovieEntity movieEntity = aRandom.movieEntity().watched(false).save()

        when:
        movieService.markMovieAsViewed(movieEntity.sessionId, true)

        then:
        MovieEntity savedMovieEntity = movieRepository.findById(movieEntity.sessionId).get()
        assert savedMovieEntity.watched == true
    }
}
