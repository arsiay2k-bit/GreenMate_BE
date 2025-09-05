package com.greenmate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenmate.dto.StepRecommendationRequest;
import com.greenmate.dto.StepRecommendationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiAgentService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${ai.agent.base-url:http://103.244.108.70:9000}")
    private String aiAgentBaseUrl;
    
    @Value("${ai.agent.step-recommendation-endpoint:/api/v1/recommend/steps}")
    private String stepRecommendationEndpoint;
    
    public StepRecommendationResponse getStepRecommendation(StepRecommendationRequest request) {
        try {
            log.info("AI 에이전트에 걸음 수 추천 요청: {}", request);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<StepRecommendationRequest> httpEntity = new HttpEntity<>(request, headers);
            
            String url = aiAgentBaseUrl + stepRecommendationEndpoint;
            ResponseEntity<StepRecommendationResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    StepRecommendationResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("AI 에이전트 응답 성공: {}", response.getBody());
                
                StepRecommendationResponse aiResponse = response.getBody();
                
                // AI 응답을 기반으로 추가 정보 계산
                if ("success".equals(aiResponse.getStatus()) && aiResponse.getData() != null) {
                    aiResponse.calculateAdditionalInfo(request.getGender(), request.getAge());
                    return aiResponse;
                } else {
                    log.warn("AI 에이전트 응답 상태가 success가 아님: {}", aiResponse.getStatus());
                    return getDefaultRecommendation(request);
                }
            } else {
                log.warn("AI 에이전트 응답이 비어있음 또는 오류, 기본값 사용");
                return getDefaultRecommendation(request);
            }
            
        } catch (ResourceAccessException e) {
            log.warn("AI 에이전트 서버에 연결할 수 없음, 기본 추천값 사용: {}", e.getMessage());
            return getDefaultRecommendation(request);
        } catch (Exception e) {
            log.error("AI 에이전트 호출 중 오류 발생, 기본 추천값 사용: {}", e.getMessage(), e);
            return getDefaultRecommendation(request);
        }
    }
    
    /**
     * AI 에이전트 서버에 연결할 수 없을 때 사용할 기본 추천값
     */
    private StepRecommendationResponse getDefaultRecommendation(StepRecommendationRequest request) {
        Integer age = request.getAge();
        String gender = request.getGender();
        Double weight = request.getWeight();
        
        // 기본 걸음 수 계산 (연령대별)
        int baseSteps = calculateBaseSteps(age);
        
        // 성별에 따른 조정
        if ("FEMALE".equals(gender)) {
            baseSteps = (int) (baseSteps * 0.95);
        }
        
        // BMI 기반 조정 (키와 몸무게가 있는 경우)
        if (request.getHeight() != null && weight != null && request.getHeight() > 0 && weight > 0) {
            double bmi = weight / Math.pow(request.getHeight() / 100, 2);
            if (bmi < 18.5) {
                baseSteps = (int) (baseSteps * 0.9); // 저체중
            } else if (bmi > 25) {
                baseSteps = (int) (baseSteps * 1.1); // 과체중
            }
        }
        
        
        // 기본 추천값을 AI 응답 형식으로 생성
        StepRecommendationResponse.RecommendationData data = 
                StepRecommendationResponse.RecommendationData.builder()
                        .recommendedSteps(baseSteps)
                        .build();
                        
        StepRecommendationResponse response = StepRecommendationResponse.builder()
                .status("success")
                .data(data)
                .build();
                
        // 추가 정보 계산
        response.calculateAdditionalInfo(request.getGender(), request.getAge());
        
        return response;
    }
    
    private int calculateBaseSteps(Integer age) {
        if (age == null) return 8000;
        
        if (age < 30) return 10000;
        else if (age < 40) return 9000;
        else if (age < 50) return 8500;
        else if (age < 60) return 8000;
        else return 7000;
    }
}