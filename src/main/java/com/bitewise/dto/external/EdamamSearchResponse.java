package com.bitewise.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdamamSearchResponse {

    @JsonProperty("q")
    private String query;

    @JsonProperty("count")
    private Integer count;

    @JsonProperty("to")
    private Integer to;

    @JsonProperty("_links")
    private Links links;

    @JsonProperty("hits")
    private List<EdamamRecipeResponse> hits;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links {

        @JsonProperty("next")
        private Link next;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Link {
            @JsonProperty("href")
            private String href;

            @JsonProperty("title")
            private String title;
        }
    }
}
