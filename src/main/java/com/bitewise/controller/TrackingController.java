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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/log-meal")
    public ResponseEntity<ApiResponse<Map<String, Object>>> logMealDirectly(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String foodName = (String) request.get("foodName");
        Double calories = request.get("calories") != null ? ((Number) request.get("calories")).doubleValue() : 0.0;
        Double protein = request.get("protein") != null ? ((Number) request.get("protein")).doubleValue() : 0.0;
        Double carbs = request.get("carbs") != null ? ((Number) request.get("carbs")).doubleValue() : 0.0;
        Double fat = request.get("fat") != null ? ((Number) request.get("fat")).doubleValue() : 0.0;
        String mealType = (String) request.get("mealType");

        // Create a simple consumption log without requiring a meal plan
        ConsumptionLog log = ConsumptionLog.builder()
                .dailyMeal(null) // No meal plan required
                .actualCalories(calories)
                .actualProtein(protein)
                .actualCarbs(carbs)
                .actualFat(fat)
                .notes(foodName + " (" + mealType + ")")
                .consumedAt(LocalDateTime.now())
                .build();

        consumptionLogRepository.save(log);

        Map<String, Object> response = new HashMap<>();
        response.put("id", log.getId());
        response.put("foodName", foodName);
        response.put("calories", calories);
        response.put("protein", protein);
        response.put("carbs", carbs);
        response.put("fat", fat);
        response.put("mealType", mealType);
        response.put("consumedAt", log.getConsumedAt());

        return ResponseEntity.ok(ApiResponse.success(response, "Comida registrada exitosamente"));
    }

    @GetMapping("/daily-consumption")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDailyConsumption(
            Authentication authentication) {
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        // Get all logs for today
        List<ConsumptionLog> todayLogs = consumptionLogRepository.findByConsumedAtBetween(startOfDay, endOfDay);

        // Calculate totals
        double totalCalories = todayLogs.stream().mapToDouble(ConsumptionLog::getActualCalories).sum();
        double totalProtein = todayLogs.stream().mapToDouble(ConsumptionLog::getActualProtein).sum();
        double totalCarbs = todayLogs.stream().mapToDouble(ConsumptionLog::getActualCarbs).sum();
        double totalFat = todayLogs.stream().mapToDouble(ConsumptionLog::getActualFat).sum();

        Map<String, Object> response = new HashMap<>();
        response.put("date", today.toString());
        response.put("totalCalories", totalCalories);
        response.put("totalProtein", totalProtein);
        response.put("totalCarbs", totalCarbs);
        response.put("totalFat", totalFat);
        response.put("mealCount", todayLogs.size());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
