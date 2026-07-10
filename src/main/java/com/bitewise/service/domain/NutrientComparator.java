package com.bitewise.service.domain;

import com.bitewise.entity.ConsumptionLog;
import com.bitewise.entity.DailyMeal;
import com.bitewise.entity.NutritionProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NutrientComparator {

    public NutrientComparisonResult compareConsumptionVsGoal(NutritionProfile profile, List<DailyMeal> dailyMeals) {
        int totalActualCalories = 0;
        double totalActualProtein = 0;
        double totalActualCarbs = 0;
        double totalActualFat = 0;

        for (DailyMeal dailyMeal : dailyMeals) {
            for (ConsumptionLog log : dailyMeal.getConsumptionLogs()) {
                totalActualCalories += log.getActualCalories();
                totalActualProtein += log.getActualProtein();
                totalActualCarbs += log.getActualCarbs();
                totalActualFat += log.getActualFat();
            }
        }

        int targetCalories = profile.getDailyCalorieTarget() != null ? profile.getDailyCalorieTarget() : 2000;
        double targetProtein = targetCalories * (profile.getProteinPercentage() != null ? profile.getProteinPercentage() / 100 : 0.25) / 4;
        double targetCarbs = targetCalories * (profile.getCarbohydratePercentage() != null ? profile.getCarbohydratePercentage() / 100 : 0.45) / 4;
        double targetFat = targetCalories * (profile.getFatPercentage() != null ? profile.getFatPercentage() / 100 : 0.30) / 9;

        return NutrientComparisonResult.builder()
                .caloriesDifference(totalActualCalories - targetCalories)
                .proteinDifference(totalActualProtein - targetProtein)
                .carbsDifference(totalActualCarbs - targetCarbs)
                .fatDifference(totalActualFat - targetFat)
                .caloriesAchieved(totalActualCalories >= targetCalories * 0.9 && totalActualCalories <= targetCalories * 1.1)
                .proteinAchieved(totalActualProtein >= targetProtein * 0.9 && totalActualProtein <= targetProtein * 1.1)
                .carbsAchieved(totalActualCarbs >= targetCarbs * 0.9 && totalActualCarbs <= targetCarbs * 1.1)
                .fatAchieved(totalActualFat >= targetFat * 0.9 && totalActualFat <= targetFat * 1.1)
                .build();
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class NutrientComparisonResult {
        private int caloriesDifference;
        private double proteinDifference;
        private double carbsDifference;
        private double fatDifference;
        private boolean caloriesAchieved;
        private boolean proteinAchieved;
        private boolean carbsAchieved;
        private boolean fatAchieved;
    }
}
