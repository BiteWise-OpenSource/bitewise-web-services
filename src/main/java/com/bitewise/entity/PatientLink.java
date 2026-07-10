package com.bitewise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patient_links")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutritionist_id", nullable = false)
    private User nutritionist;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LinkStatus status = LinkStatus.PENDING;

    @Column(length = 500)
    private String notes;

    @Builder.Default
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "patient_link_id")
    private Set<Observation> observations = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private LocalDateTime linkedAt;

    @Column(nullable = true)
    private LocalDateTime revokedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == LinkStatus.ACTIVE && linkedAt == null) {
            linkedAt = LocalDateTime.now();
        }
        if (status == LinkStatus.REVOKED && revokedAt == null) {
            revokedAt = LocalDateTime.now();
        }
    }
}
