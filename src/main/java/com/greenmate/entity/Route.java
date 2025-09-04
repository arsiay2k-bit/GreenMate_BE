package com.greenmate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    private String routeType;
    private Double distanceKm;
    private Integer durationMinutes;
    private Integer estimatedSteps;
    private Location startLocation;
    private Location endLocation;
    private List<Location> waypoints;
    private String polyline;
}