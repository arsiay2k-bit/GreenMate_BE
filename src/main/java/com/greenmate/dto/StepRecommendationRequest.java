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
public class StepRecommendationRequest {
    
    private String userId;
    private String gender;
    private Integer age;
    private Double height;
    private Double weight;
    
    public static StepRecommendationRequest fromUser(User user) {
        // 성별을 한글로 변환
        String genderInKorean = convertGenderToKorean(user.getGender());
        
        return StepRecommendationRequest.builder()
                .userId(user.getNickname()) // 닉네임을 userId로 사용
                .gender(genderInKorean)
                .age(user.getAge())
                .height(user.getHeight())
                .weight(user.getWeight())
                .build();
    }
    
    private static String convertGenderToKorean(User.Gender gender) {
        if (gender == null) return null;
        
        switch (gender) {
            case MALE:
                return "남";
            case FEMALE:
                return "여";
            case OTHER:
                return "기타";
            default:
                return null;
        }
    }
}