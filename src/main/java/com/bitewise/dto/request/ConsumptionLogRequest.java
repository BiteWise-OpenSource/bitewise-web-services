package com.bitewise.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumptionLogRequest {

    @NotNull
    @Positive
    private Integer actualCalories;

    @NotNull
    private Double actualProtein;

    @NotNull
    private Double actualCarbs;

    @NotNull
    private Double actualFat;

    private String notes;
}
