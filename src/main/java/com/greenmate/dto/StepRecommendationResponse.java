package com.greenmate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StepRecommendationResponse {
    
    private String status;
    private RecommendationData data;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationData {
        private Integer recommendedSteps;
    }
    
    // 계산된 추천 정보를 위한 추가 필드들 (AI 에이전트 응답에서 파생됨)
    private Integer dailySteps;
    private Double dailyCaloriesBurn;
    private Integer dailyWalkingTimeMinutes;
    private String personalizedAdvice;
    private String fitnessLevel;
    private String activityType;
    
    /**
     * AI 에이전트 응답을 기반으로 추가 정보를 계산하여 설정하는 메서드
     */
    public void calculateAdditionalInfo(String gender, Integer age) {
        if (data != null && data.getRecommendedSteps() != null) {
            this.dailySteps = data.getRecommendedSteps();
            
            // 칼로리 소모량 계산 (걸음당 약 0.04-0.05 kcal)
            this.dailyCaloriesBurn = dailySteps * 0.045;
            
            // 걷기 시간 계산 (분당 약 100걸음 기준)
            this.dailyWalkingTimeMinutes = dailySteps / 100;
            
            // 개인맞춤 조언 생성
            this.personalizedAdvice = generateAdvice(dailySteps, gender, age);
            
            // 피트니스 레벨 결정
            this.fitnessLevel = determineFitnessLevel(dailySteps);
            
            this.activityType = "걷기";
        }
    }
    
    private String generateAdvice(Integer steps, String gender, Integer age) {
        StringBuilder advice = new StringBuilder();
        
        advice.append(String.format("AI가 분석한 결과 하루 %,d걸음을 권장합니다. ", steps));
        
        if (age != null && age < 30) {
            advice.append("젊은 나이이므로 활발한 활동을 권장합니다. ");
        } else if (age != null && age > 60) {
            advice.append("안전한 범위에서 꾸준히 걷기를 실천하세요. ");
        }
        
        advice.append("주 3-4회 이상 규칙적으로 걷기를 실천하시고, ");
        advice.append("걷기 전후 스트레칭을 잊지 마세요.");
        
        return advice.toString();
    }
    
    private String determineFitnessLevel(Integer steps) {
        if (steps >= 10000) return "높음";
        else if (steps >= 8000) return "보통";
        else return "낮음";
    }
}