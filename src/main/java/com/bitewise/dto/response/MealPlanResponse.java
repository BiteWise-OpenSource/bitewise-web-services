package com.bitewise.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanResponse {

    private Long id;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer dailyCalorieTarget;
    private Double proteinPercentage;
    private Double carbohydratePercentage;
    private Double fatPercentage;
    private String primaryGoal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
