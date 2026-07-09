package com.bitewise.repository;

import com.bitewise.entity.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, Long> {
    Optional<Allergy> findByName(String name);
    boolean existsByName(String name);
}
