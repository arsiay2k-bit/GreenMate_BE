package com.greenmate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalkRecordRequest {
    @NotNull
    private Double startLatitude;
    
    @NotNull
    private Double startLongitude;
    
    private String startAddress;
    
    @NotNull
    private Double endLatitude;
    
    @NotNull
    private Double endLongitude;
    
    private String endAddress;
    
    @NotNull
    @Positive
    private Double distanceKm;
    
    @NotNull
    @Positive
    private Integer durationMinutes;
    
    @NotNull
    @Positive
    private Integer steps;
    
    private String routeType;
}