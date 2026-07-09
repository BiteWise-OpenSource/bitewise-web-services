package com.bitewise.repository;

import com.bitewise.entity.DietaryPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DietaryPreferenceRepository extends JpaRepository<DietaryPreference, Long> {
    Optional<DietaryPreference> findByName(String name);
    boolean existsByName(String name);
}
