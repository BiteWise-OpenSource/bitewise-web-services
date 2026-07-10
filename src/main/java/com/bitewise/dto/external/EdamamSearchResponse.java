package com.bitewise.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdamamSearchResponse {

    private Integer from;

    private Integer to;

    private Integer count;

    private List<Hit> hits;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hit {
        private EdamamRecipeResponse recipe;
    }
}
