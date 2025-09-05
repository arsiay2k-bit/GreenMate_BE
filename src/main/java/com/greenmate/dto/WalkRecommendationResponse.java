package com.greenmate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalkRecommendationResponse {
    
    private UserProfile userProfile;
    private RecommendationInfo recommendationInfo;
    private List<RouteOption> routeOptions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfile {
        private String gender;
        private Integer age;
        private Double weight;
        private Double height;
        private Integer dailyStepsRecommendation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationInfo {
        private Integer recommendedSteps;
        private Integer todaySteps;
        private Integer remainingSteps;
        private String message;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteOption {
        private String routeName;
        private Double distanceKm;
        private Integer durationMinutes;
        private Integer estimatedSteps;
        private String routeType;
        private LocationInfo startLocation;
        private LocationInfo endLocation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationInfo {
        private String address;
        private Double latitude;
        private Double longitude;
    }
}