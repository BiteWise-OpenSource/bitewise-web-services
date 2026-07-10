package com.bitewise.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealPlanRequest {

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
