package com.alamobot.core.persistence;

import com.alamobot.core.domain.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SeatRepository extends JpaRepository<SeatEntity, Integer> {
    @Transactional
    void deleteBySessionId(int sessionId);

    List<SeatEntity> findAllBySessionId(int sessionId);

    SeatEntity findBySessionIdAndRowNumberAndSeatNumber(int sessionId, int rowNumber, int seatNumber);

    List<SeatEntity> findByIdIn(List<Integer> idList);

    List<SeatEntity> findAllBySeatBoughtIsTrue();

    List<SeatEntity> findAllBySeatBought(boolean seatBought);

    List<SeatEntity> findAllBySessionIdAndSeatBought(int sessionId, boolean seatBought);

    List<SeatEntity> findAllBySessionIdAndSeatStatusAndSeatBoughtAndRowIndexGreaterThanEqual(int sessionId, String seatStatus, boolean seatBought, int rowIndex);

    List<SeatEntity> findAllBySessionIdAndSeatStatusAndSeatBoughtAndRowIndexGreaterThanEqualAndSeatStyleNotIn(int sessionId, String seatStatus, boolean seatBought, int rowIndex, List<String> seatStylesToExclude);

    List<SeatEntity> findAllBySessionIdAndSeatStatusAndSeatBought(int sessionId, String seatStatus, boolean seatBought);

    List<SeatEntity> findAllBySessionIdAndSeatStatusAndSeatBoughtAndSeatStyleNotIn(int sessionId, String seatStatus, boolean seatBought, List<String> seatStylesToExclude);
}
