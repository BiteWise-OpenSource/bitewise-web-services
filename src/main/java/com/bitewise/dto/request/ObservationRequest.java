package com.bitewise.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservationRequest {

    @NotNull
    private Long mealPlanId;

    @NotBlank
    private String comment;
}
