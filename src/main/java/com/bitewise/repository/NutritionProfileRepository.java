package com.bitewise.repository;

import com.bitewise.entity.NutritionProfile;
import com.bitewise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NutritionProfileRepository extends JpaRepository<NutritionProfile, Long> {
    Optional<NutritionProfile> findByUser(User user);
    Optional<NutritionProfile> findByUserId(Long userId);
}
