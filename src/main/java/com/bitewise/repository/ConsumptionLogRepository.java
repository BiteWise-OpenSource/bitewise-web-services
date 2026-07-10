package com.bitewise.repository;

import com.bitewise.entity.ConsumptionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsumptionLogRepository extends JpaRepository<ConsumptionLog, Long> {
    List<ConsumptionLog> findByDailyMealId(Long dailyMealId);
    List<ConsumptionLog> findByDailyMealIdAndConsumedAtBetween(Long dailyMealId, LocalDateTime start, LocalDateTime end);
}
