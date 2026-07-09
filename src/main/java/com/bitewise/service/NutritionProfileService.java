package com.bitewise.service;

import com.bitewise.dto.request.CreateNutritionProfileRequest;
import com.bitewise.dto.request.UpdateNutritionProfileRequest;
import com.bitewise.dto.response.NutritionProfileResponse;

public interface NutritionProfileService {
    NutritionProfileResponse createProfile(Long userId, CreateNutritionProfileRequest request);
    NutritionProfileResponse getProfileByUserId(Long userId);
    NutritionProfileResponse updateProfile(Long userId, UpdateNutritionProfileRequest request);
    void addAllergy(Long profileId, Long allergyId);
    void removeAllergy(Long profileId, Long allergyId);
    void addDietaryPreference(Long profileId, Long preferenceId);
    void removeDietaryPreference(Long profileId, Long preferenceId);
}
