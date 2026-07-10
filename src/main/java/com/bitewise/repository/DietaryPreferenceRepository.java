package com.bitewise.repository;

import com.bitewise.entity.DietaryPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DietaryPreferenceRepository extends JpaRepository<DietaryPreference, Long> {

    Optional<DietaryPreference> findByNameIgnoreCase(String name);

}