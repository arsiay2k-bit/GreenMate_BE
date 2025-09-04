package com.greenmate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "walk_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalkRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "start_latitude", nullable = false)
    private Double startLatitude;
    
    @Column(name = "start_longitude", nullable = false)
    private Double startLongitude;
    
    @Column(name = "start_address")
    private String startAddress;
    
    @Column(name = "end_latitude", nullable = false)
    private Double endLatitude;
    
    @Column(name = "end_longitude", nullable = false)
    private Double endLongitude;
    
    @Column(name = "end_address")
    private String endAddress;
    
    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;
    
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    
    @Column(name = "steps", nullable = false)
    private Integer steps;
    
    @Column(name = "route_type")
    private String routeType;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}