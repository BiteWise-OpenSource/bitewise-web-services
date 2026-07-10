package com.bitewise.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdamamRecipeResponse {

    private String uri;

    private String label;

    private String image;

    private String source;

    private String url;

    private Double yield;

    private List<String> dietLabels;

    private List<String> healthLabels;

    private List<String> ingredientLines;

    private Double calories;

    private Double totalTime;

    private Map<String, Nutrient> totalNutrients;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Nutrient {
        private String label;
        private Double quantity;
        private String unit;
    }
}
