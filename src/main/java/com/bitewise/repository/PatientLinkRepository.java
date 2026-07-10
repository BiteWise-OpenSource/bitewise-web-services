package com.bitewise.repository;

import com.bitewise.entity.LinkStatus;
import com.bitewise.entity.PatientLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientLinkRepository extends JpaRepository<PatientLink, Long> {
    Optional<PatientLink> findByPatientIdAndNutritionistId(Long patientId, Long nutritionistId);
    List<PatientLink> findByPatientId(Long patientId);
    List<PatientLink> findByNutritionistId(Long nutritionistId);
    List<PatientLink> findByPatientIdAndStatus(Long patientId, LinkStatus status);
    List<PatientLink> findByNutritionistIdAndStatus(Long nutritionistId, LinkStatus status);
}
