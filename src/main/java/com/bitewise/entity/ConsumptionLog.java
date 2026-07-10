package com.bitewise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consumption_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumptionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_meal_id", nullable = false)
    private DailyMeal dailyMeal;

    @Column(nullable = false)
    private Integer actualCalories;

    @Column(nullable = false)
    private Double actualProtein;

    @Column(nullable = false)
    private Double actualCarbs;

    @Column(nullable = false)
    private Double actualFat;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime consumedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (consumedAt == null) {
            consumedAt = LocalDateTime.now();
        }
    }
}
