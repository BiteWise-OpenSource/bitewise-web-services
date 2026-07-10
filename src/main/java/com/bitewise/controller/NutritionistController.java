package com.bitewise.controller;

import com.bitewise.dto.request.ObservationRequest;
import com.bitewise.dto.request.PatientLinkRequest;
import com.bitewise.dto.response.ApiResponse;
import com.bitewise.entity.LinkStatus;
import com.bitewise.entity.Observation;
import com.bitewise.entity.PatientLink;
import com.bitewise.entity.User;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.repository.ObservationRepository;
import com.bitewise.repository.PatientLinkRepository;
import com.bitewise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nutritionist")
@RequiredArgsConstructor
public class NutritionistController {

    private final PatientLinkRepository patientLinkRepository;
    private final ObservationRepository observationRepository;
    private final UserRepository userRepository;

    @PostMapping("/link-patient")
    public ResponseEntity<ApiResponse<Void>> linkPatient(
            @RequestBody PatientLinkRequest request,
            Authentication authentication) {
        
        String email = authentication.getName();
        User nutritionist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Nutricionista no encontrado"));

        User patient = userRepository.findById(request.getNutritionistId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        if (patientLinkRepository.findByPatientIdAndNutritionistId(patient.getId(), nutritionist.getId()).isPresent()) {
            throw new ResourceNotFoundException("Ya existe un vínculo con este paciente");
        }

        PatientLink link = PatientLink.builder()
                .patient(patient)
                .nutritionist(nutritionist)
                .status(LinkStatus.PENDING)
                .notes(request.getNotes())
                .build();

        patientLinkRepository.save(link);

        return ResponseEntity.ok(ApiResponse.success(null, "Solicitud de vínculo enviada exitosamente"));
    }

    @GetMapping("/patients")
    public ResponseEntity<ApiResponse<List<PatientLink>>> getLinkedPatients(Authentication authentication) {
        String email = authentication.getName();
        User nutritionist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Nutricionista no encontrado"));

        List<PatientLink> links = patientLinkRepository.findByNutritionistIdAndStatus(
                nutritionist.getId(), LinkStatus.ACTIVE);

        return ResponseEntity.ok(ApiResponse.success(links));
    }

    @PostMapping("/observations")
    public ResponseEntity<ApiResponse<Void>> addObservation(
            @RequestBody ObservationRequest request,
            Authentication authentication) {
        
        String email = authentication.getName();
        User nutritionist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Nutricionista no encontrado"));

        PatientLink link = patientLinkRepository.findByNutritionistId(nutritionist.getId())
                .stream()
                .filter(l -> l.getPatient().getId().equals(request.getMealPlanId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No tienes vínculo con este paciente"));

        Observation observation = Observation.builder()
                .patientLink(link)
                .comment(request.getComment())
                .build();

        observationRepository.save(observation);

        return ResponseEntity.ok(ApiResponse.success(null, "Observación agregada exitosamente"));
    }

    @GetMapping("/observations/{patientLinkId}")
    public ResponseEntity<ApiResponse<List<Observation>>> getObservations(
            @PathVariable Long patientLinkId,
            Authentication authentication) {
        
        String email = authentication.getName();
        User nutritionist = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Nutricionista no encontrado"));

        PatientLink link = patientLinkRepository.findById(patientLinkId)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo no encontrado"));

        if (!link.getNutritionist().getId().equals(nutritionist.getId())) {
            throw new ResourceNotFoundException("No tienes permiso para ver estas observaciones");
        }

        List<Observation> observations = observationRepository.findByPatientLinkId(patientLinkId);

        return ResponseEntity.ok(ApiResponse.success(observations));
    }
}
