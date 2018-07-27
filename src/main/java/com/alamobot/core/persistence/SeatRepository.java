package com.alamobot.core.persistence;

import com.alamobot.core.domain.SeatEntity;
import com.alamobot.core.domain.SeatEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<SeatEntity, SeatEntityId> {
    void deleteBySessionId(int sessionId);

    List<SeatEntity> findAllBySessionId(int sessionId);
}
