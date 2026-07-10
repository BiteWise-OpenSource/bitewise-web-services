package com.bitewise.external;

import com.bitewise.dto.external.EdamamRecipeResponse;
import com.bitewise.dto.external.EdamamSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Component
public class EdamamRecipeAdapter {

    private final RestClient restClient = RestClient.create();

    @Value("${edamam.api.url}")
    private String apiUrl;

    @Value("${edamam.api.app-id}")
    private String appId;

    @Value("${edamam.api.app-key}")
    private String appKey;

    public List<EdamamRecipeResponse> searchRecipes(String query) {
        EdamamSearchResponse response = restClient.get()
                .uri(apiUrl + "?type=public&q={q}&app_id={id}&app_key={key}", query, appId, appKey)
                .retrieve()
                .body(EdamamSearchResponse.class);

        if (response == null || response.getHits() == null) {
            return Collections.emptyList();
        }

        return response.getHits().stream()
                .map(EdamamSearchResponse.Hit::getRecipe)
                .toList();
    }
}
