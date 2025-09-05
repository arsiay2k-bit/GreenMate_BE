package com.greenmate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalkRecommendationRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotBlank(message = "시작 위치 키워드는 필수입니다")
    private String startKeyword;
    
    @NotBlank(message = "시작 위치 타입은 필수입니다")
    private String startLocationType;
    
    @NotBlank(message = "시작 지역은 필수입니다")
    private String startRegion;
    
    @NotBlank(message = "도착 위치 키워드는 필수입니다")
    private String endKeyword;
    
    @NotBlank(message = "도착 위치 타입은 필수입니다")
    private String endLocationType;
    
    @NotBlank(message = "도착 지역은 필수입니다")
    private String endRegion;
    
    @NotNull(message = "오늘 걸음 수는 필수입니다")
    private Integer todaySteps;
}