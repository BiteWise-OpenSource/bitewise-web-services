package com.bitewise.service.domain;

import com.bitewise.entity.Subscription;
import com.bitewise.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionGate {

    private final SubscriptionRepository subscriptionRepository;

    public boolean isPremium(Long userId) {
        return subscriptionRepository.findByUserId(userId)
                .map(Subscription::isPremium)
                .orElse(false);
    }

    public boolean canCreateMultipleMealPlans(Long userId) {
        return isPremium(userId);
    }

    public boolean canAccessAdvancedRecipes(Long userId) {
        return isPremium(userId);
    }

    public boolean canAccessNutritionistFeatures(Long userId) {
        return isPremium(userId);
    }
}
