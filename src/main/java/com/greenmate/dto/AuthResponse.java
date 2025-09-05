package com.greenmate.dto;

import com.greenmate.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String accessToken;
    private String tokenType;
    private Long userId;
    private String email;
    private String nickname;
    private User.Gender gender;
    private Integer age;
    private Double height;
    private Double weight;
    private StepRecommendationInfo stepRecommendations;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StepRecommendationInfo {
        private Integer dailySteps;
        private Double dailyCaloriesBurn;
        private Integer dailyWalkingTimeMinutes;
        private String personalizedAdvice;
    }
    
    public static AuthResponse from(String accessToken, User user) {
        StepRecommendationInfo recommendations = null;
        if (user.getDailyStepsRecommendation() != null) {
            recommendations = StepRecommendationInfo.builder()
                    .dailySteps(user.getDailyStepsRecommendation())
                    .dailyCaloriesBurn(user.getDailyCaloriesBurnRecommendation())
                    .dailyWalkingTimeMinutes(user.getDailyWalkingTimeRecommendation())
                    .personalizedAdvice(user.getPersonalizedRecommendations())
                    .build();
        }
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .age(user.getAge())
                .height(user.getHeight())
                .weight(user.getWeight())
                .stepRecommendations(recommendations)
                .build();
    }
}