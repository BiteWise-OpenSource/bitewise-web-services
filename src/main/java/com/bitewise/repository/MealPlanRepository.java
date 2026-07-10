package com.bitewise.repository;

import com.bitewise.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    Optional<MealPlan> findByUserId(Long userId);
    List<MealPlan> findByUserIdAndStartDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    Optional<MealPlan> findByUserIdAndEndDateAfter(Long userId, LocalDate date);
    boolean existsByUserIdAndEndDateAfter(Long userId, LocalDate date);
}
