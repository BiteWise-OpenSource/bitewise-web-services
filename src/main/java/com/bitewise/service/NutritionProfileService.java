package com.bitewise.service;

import com.bitewise.dto.request.NutritionProfileRequest;
import com.bitewise.dto.response.NutritionProfileResponse;

public interface NutritionProfileService {

    NutritionProfileResponse createOrUpdate(String email, NutritionProfileRequest request);

    NutritionProfileResponse getByUser(String email);

    void delete(String email);
}
