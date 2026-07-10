package com.bitewise.repository;

import com.bitewise.entity.Observation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, Long> {
    List<Observation> findByPatientLinkId(Long patientLinkId);
    List<Observation> findByPatientLinkIdAndIsReviewed(Long patientLinkId, Boolean isReviewed);
}
