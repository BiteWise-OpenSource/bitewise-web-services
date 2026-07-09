package com.bitewise.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdamamRecipeResponse {

    @JsonProperty("recipe")
    private Recipe recipe;

    @JsonProperty("bookmarked")
    private Boolean bookmarked;

    @JsonProperty("bought")
    private Boolean bought;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Recipe {

        @JsonProperty("uri")
        private String uri;

        @JsonProperty("label")
        private String label;

        @JsonProperty("image")
        private String image;

        @JsonProperty("source")
        private String source;

        @JsonProperty("url")
        private String url;

        @JsonProperty("yield")
        private Double yield;

        @JsonProperty("ingredientLines")
        private List<IngredientLine> ingredientLines;

        @JsonProperty("calories")
        private Double calories;

        @JsonProperty("totalTime")
        private Integer totalTime;

        @JsonProperty("cuisineType")
        private List<String> cuisineType;

        @JsonProperty("mealType")
        private List<String> mealType;

        @JsonProperty("dietLabels")
        private List<String> dietLabels;

        @JsonProperty("healthLabels")
        private List<String> healthLabels;

        @JsonProperty("totalNutrients")
        private Map<String, Nutrient> totalNutrients;

        @JsonProperty("totalDaily")
        private Map<String, Nutrient> totalDaily;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class IngredientLine {

            @JsonProperty("food")
            private Food food;

            @JsonProperty("weight")
            private Double weight;

            @JsonProperty("quantity")
            private Double quantity;

            @JsonProperty("measure")
            private String measure;

            @Getter
            @Setter
            @NoArgsConstructor
            @AllArgsConstructor
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Food {

                @JsonProperty("foodId")
                private String foodId;

                @JsonProperty("label")
                private String label;

                @JsonProperty("knownAs")
                private String knownAs;

                @JsonProperty("nutrients")
                private Map<String, Double> nutrients;

                @JsonProperty("category")
                private String category;

                @JsonProperty("categoryLabel")
                private String categoryLabel;

                @JsonProperty("image")
                private String image;
            }
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Nutrient {

            @JsonProperty("label")
            private String label;

            @JsonProperty("quantity")
            private Double quantity;

            @JsonProperty("unit")
            private String unit;
        }
    }
}
