package com.bitewise.controller;

import com.bitewise.dto.request.ConsumptionLogRequest;
import com.bitewise.dto.response.ApiResponse;
import com.bitewise.entity.ConsumptionLog;
import com.bitewise.entity.DailyMeal;
import com.bitewise.entity.User;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.repository.ConsumptionLogRepository;
import com.bitewise.repository.DailyMealRepository;
import com.bitewise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final ConsumptionLogRepository consumptionLogRepository;
    private final DailyMealRepository dailyMealRepository;
    private final UserRepository userRepository;

    @PostMapping("/daily-meal/{dailyMealId}/log")
    public ResponseEntity<ApiResponse<Void>> logConsumption(
            @PathVariable Long dailyMealId,
            @RequestBody ConsumptionLogRequest request,
            Authentication authentication) {
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        DailyMeal dailyMeal = dailyMealRepository.findById(dailyMealId)
                .orElseThrow(() -> new ResourceNotFoundException("Comida diaria no encontrada"));

        if (!dailyMeal.getMealPlan().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("No tienes permiso para registrar esta comida");
        }

        ConsumptionLog log = ConsumptionLog.builder()
                .dailyMeal(dailyMeal)
                .actualCalories(request.getActualCalories())
                .actualProtein(request.getActualProtein())
                .actualCarbs(request.getActualCarbs())
                .actualFat(request.getActualFat())
                .notes(request.getNotes())
                .consumedAt(LocalDateTime.now())
                .build();

        consumptionLogRepository.save(log);

        dailyMeal.setIsConsumed(true);
        dailyMealRepository.save(dailyMeal);

        return ResponseEntity.ok(ApiResponse.success(null, "Consumo registrado exitosamente"));
    }

    @GetMapping("/daily-meal/{dailyMealId}/logs")
    public ResponseEntity<ApiResponse<java.util.List<ConsumptionLog>>> getConsumptionLogs(
            @PathVariable Long dailyMealId,
            Authentication authentication) {
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        DailyMeal dailyMeal = dailyMealRepository.findById(dailyMealId)
                .orElseThrow(() -> new ResourceNotFoundException("Comida diaria no encontrada"));

        if (!dailyMeal.getMealPlan().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("No tienes permiso para ver estos registros");
        }

        java.util.List<ConsumptionLog> logs = consumptionLogRepository.findByDailyMealId(dailyMealId);

        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
