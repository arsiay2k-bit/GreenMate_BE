package com.greenmate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequest {
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
}