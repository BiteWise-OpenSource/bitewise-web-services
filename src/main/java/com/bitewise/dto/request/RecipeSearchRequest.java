package com.bitewise.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeSearchRequest {

    @NotBlank
    private String query;

    private Integer from;

    private Integer to;

    private Boolean vegetarian;

    private Boolean vegan;

    private Boolean glutenFree;

    private Boolean dairyFree;

    private Boolean keto;

}