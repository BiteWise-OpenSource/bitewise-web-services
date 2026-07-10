package com.bitewise.controller;

import com.bitewise.dto.request.MealPlanRequest;
import com.bitewise.dto.response.ApiResponse;
import com.bitewise.dto.response.MealPlanResponse;
import com.bitewise.entity.MealPlan;
import com.bitewise.entity.NutritionProfile;
import com.bitewise.entity.User;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.repository.MealPlanRepository;
import com.bitewise.repository.NutritionProfileRepository;
import com.bitewise.repository.UserRepository;
import com.bitewise.service.domain.MealPlanGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meal-plans")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanRepository mealPlanRepository;
    private final UserRepository userRepository;
    private final NutritionProfileRepository nutritionProfileRepository;
    private final MealPlanGenerator mealPlanGenerator;

    @PostMapping
    public ResponseEntity<ApiResponse<MealPlanResponse>> createMealPlan(
            @RequestBody MealPlanRequest request,
            Authentication authentication) {
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        NutritionProfile profile = nutritionProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil nutricional no encontrado"));

        MealPlan mealPlan = mealPlanGenerator.generateMealPlan(
                user, profile, request.getStartDate(), request.getEndDate());

        MealPlan savedMealPlan = mealPlanRepository.save(mealPlan);

        MealPlanResponse response = MealPlanResponse.builder()
                .id(savedMealPlan.getId())
                .userId(savedMealPlan.getUser().getId())
                .startDate(savedMealPlan.getStartDate())
                .endDate(savedMealPlan.getEndDate())
                .dailyCalorieTarget(savedMealPlan.getDailyCalorieTarget())
                .proteinPercentage(savedMealPlan.getProteinPercentage())
                .carbohydratePercentage(savedMealPlan.getCarbohydratePercentage())
                .fatPercentage(savedMealPlan.getFatPercentage())
                .primaryGoal(savedMealPlan.getPrimaryGoal().name())
                .createdAt(savedMealPlan.getCreatedAt())
                .updatedAt(savedMealPlan.getUpdatedAt())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "Plan de comidas creado exitosamente"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<MealPlanResponse>> getCurrentMealPlan(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        MealPlan mealPlan = mealPlanRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No hay plan de comidas activo"));

        MealPlanResponse response = MealPlanResponse.builder()
                .id(mealPlan.getId())
                .userId(mealPlan.getUser().getId())
                .startDate(mealPlan.getStartDate())
                .endDate(mealPlan.getEndDate())
                .dailyCalorieTarget(mealPlan.getDailyCalorieTarget())
                .proteinPercentage(mealPlan.getProteinPercentage())
                .carbohydratePercentage(mealPlan.getCarbohydratePercentage())
                .fatPercentage(mealPlan.getFatPercentage())
                .primaryGoal(mealPlan.getPrimaryGoal().name())
                .createdAt(mealPlan.getCreatedAt())
                .updatedAt(mealPlan.getUpdatedAt())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MealPlanResponse>> updateMealPlan(
            @PathVariable Long id,
            @RequestBody MealPlanRequest request,
            Authentication authentication) {
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        MealPlan mealPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan de comidas no encontrado"));

        if (!mealPlan.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("No tienes permiso para modificar este plan");
        }

        mealPlan.setStartDate(request.getStartDate());
        mealPlan.setEndDate(request.getEndDate());

        MealPlan updatedMealPlan = mealPlanRepository.save(mealPlan);

        MealPlanResponse response = MealPlanResponse.builder()
                .id(updatedMealPlan.getId())
                .userId(updatedMealPlan.getUser().getId())
                .startDate(updatedMealPlan.getStartDate())
                .endDate(updatedMealPlan.getEndDate())
                .dailyCalorieTarget(updatedMealPlan.getDailyCalorieTarget())
                .proteinPercentage(updatedMealPlan.getProteinPercentage())
                .carbohydratePercentage(updatedMealPlan.getCarbohydratePercentage())
                .fatPercentage(updatedMealPlan.getFatPercentage())
                .primaryGoal(updatedMealPlan.getPrimaryGoal().name())
                .createdAt(updatedMealPlan.getCreatedAt())
                .updatedAt(updatedMealPlan.getUpdatedAt())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "Plan de comidas actualizado exitosamente"));
    }
}
