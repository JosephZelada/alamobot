package com.alamobot.core.persistence;

import com.alamobot.core.domain.FilmAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<FilmAlertEntity, Integer> {
    FilmAlertEntity findByFilmNameContainingIgnoreCase(String filmName);
}
