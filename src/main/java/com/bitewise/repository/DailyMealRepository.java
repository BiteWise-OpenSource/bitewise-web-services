package com.bitewise.repository;

import com.bitewise.entity.DailyMeal;
import com.bitewise.entity.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyMealRepository extends JpaRepository<DailyMeal, Long> {
    List<DailyMeal> findByMealPlanId(Long mealPlanId);
    Optional<DailyMeal> findByMealPlanIdAndMealDateAndMealType(Long mealPlanId, LocalDate mealDate, MealType mealType);
    List<DailyMeal> findByMealPlanIdAndMealDateBetween(Long mealPlanId, LocalDate startDate, LocalDate endDate);
}
