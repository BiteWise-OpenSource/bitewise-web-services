package com.bitewise.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientLinkRequest {

    @NotNull
    private Long nutritionistId;

    private String notes;
}
