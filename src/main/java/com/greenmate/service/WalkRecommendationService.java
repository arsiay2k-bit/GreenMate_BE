package com.greenmate.service;

import com.greenmate.dto.WalkRecommendationRequest;
import com.greenmate.dto.WalkRecommendationResponse;
import com.greenmate.entity.User;
import com.greenmate.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalkRecommendationService {
    
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${geocode.api.url:/api/v1/geocode}")
    private String geocodeApiUrl;
    
    public WalkRecommendationResponse getWalkRecommendation(WalkRecommendationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        int recommendedSteps = calculateDailyStepsRecommendation(user);
        int remainingSteps = Math.max(0, recommendedSteps - request.getTodaySteps());
        
        WalkRecommendationResponse.LocationInfo startLocation = geocodeLocation(
                request.getStartKeyword(), request.getStartLocationType(), request.getStartRegion());
        WalkRecommendationResponse.LocationInfo endLocation = geocodeLocation(
                request.getEndKeyword(), request.getEndLocationType(), request.getEndRegion());
        
        List<WalkRecommendationResponse.RouteOption> routeOptions = generateRouteOptions(
                startLocation, endLocation, remainingSteps);
        
        return WalkRecommendationResponse.builder()
                .userProfile(WalkRecommendationResponse.UserProfile.builder()
                        .gender(user.getGender().getDescription())
                        .age(user.getAge())
                        .weight(user.getWeight())
                        .height(user.getHeight())
                        .dailyStepsRecommendation(recommendedSteps)
                        .build())
                .recommendationInfo(WalkRecommendationResponse.RecommendationInfo.builder()
                        .recommendedSteps(recommendedSteps)
                        .todaySteps(request.getTodaySteps())
                        .remainingSteps(remainingSteps)
                        .message(generateRecommendationMessage(remainingSteps))
                        .build())
                .routeOptions(routeOptions)
                .build();
    }
    
    private int calculateDailyStepsRecommendation(User user) {
        if (user.getDailyStepsRecommendation() != null) {
            return user.getDailyStepsRecommendation();
        }
        
        int baseSteps = 8000;
        
        if (user.getAge() != null) {
            if (user.getAge() < 30) {
                baseSteps = 10000;
            } else if (user.getAge() > 60) {
                baseSteps = 6000;
            }
        }
        
        if (user.getGender() == User.Gender.MALE) {
            baseSteps += 1000;
        }
        
        if (user.getWeight() != null && user.getHeight() != null) {
            double bmi = user.getWeight() / Math.pow(user.getHeight() / 100, 2);
            if (bmi > 25) {
                baseSteps += 2000;
            }
        }
        
        return baseSteps;
    }
    
    private WalkRecommendationResponse.LocationInfo geocodeLocation(String keyword, String locationType, String region) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("keyword", keyword);
            requestBody.put("locationType", locationType);
            requestBody.put("region", region);
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    geocodeApiUrl, HttpMethod.POST, entity, String.class);
            
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            
            if (jsonResponse.has("latitude") && jsonResponse.has("longitude")) {
                return WalkRecommendationResponse.LocationInfo.builder()
                        .address(jsonResponse.path("address").asText(keyword))
                        .latitude(jsonResponse.path("latitude").asDouble())
                        .longitude(jsonResponse.path("longitude").asDouble())
                        .build();
            }
            
        } catch (Exception e) {
            log.error("Geocoding 실패: {}", e.getMessage());
        }
        
        return WalkRecommendationResponse.LocationInfo.builder()
                .address(keyword)
                .latitude(37.5665)
                .longitude(126.9780)
                .build();
    }
    
    private List<WalkRecommendationResponse.RouteOption> generateRouteOptions(
            WalkRecommendationResponse.LocationInfo start,
            WalkRecommendationResponse.LocationInfo end,
            int remainingSteps) {
        
        List<WalkRecommendationResponse.RouteOption> options = new ArrayList<>();
        
        double baseDistance = calculateDistance(
                start.getLatitude(), start.getLongitude(),
                end.getLatitude(), end.getLongitude());
        
        int baseSteps = (int) (baseDistance * 1000 / 0.7);
        int baseDuration = (int) (baseDistance * 12);
        
        options.add(WalkRecommendationResponse.RouteOption.builder()
                .routeName("직접 경로")
                .distanceKm(baseDistance)
                .durationMinutes(baseDuration)
                .estimatedSteps(baseSteps)
                .routeType("DIRECT")
                .startLocation(start)
                .endLocation(end)
                .build());
        
        if (remainingSteps > baseSteps) {
            double extendedDistance = baseDistance * 1.3;
            int extendedSteps = (int) (extendedDistance * 1000 / 0.7);
            int extendedDuration = (int) (extendedDistance * 12);
            
            options.add(WalkRecommendationResponse.RouteOption.builder()
                    .routeName("권장 경로 (추가 걸음)")
                    .distanceKm(extendedDistance)
                    .durationMinutes(extendedDuration)
                    .estimatedSteps(extendedSteps)
                    .routeType("RECOMMENDED")
                    .startLocation(start)
                    .endLocation(end)
                    .build());
        }
        
        if (remainingSteps > baseSteps * 1.5) {
            double longDistance = baseDistance * 1.6;
            int longSteps = (int) (longDistance * 1000 / 0.7);
            int longDuration = (int) (longDistance * 12);
            
            options.add(WalkRecommendationResponse.RouteOption.builder()
                    .routeName("운동 경로 (긴 거리)")
                    .distanceKm(longDistance)
                    .durationMinutes(longDuration)
                    .estimatedSteps(longSteps)
                    .routeType("EXERCISE")
                    .startLocation(start)
                    .endLocation(end)
                    .build());
        }
        
        return options;
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371;
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return earthRadius * c;
    }
    
    private String generateRecommendationMessage(int remainingSteps) {
        if (remainingSteps == 0) {
            return "오늘 권장 걸음수를 모두 달성하셨습니다! 훌륭해요!";
        } else if (remainingSteps < 2000) {
            return String.format("목표까지 %,d걸음 남았어요. 조금만 더 걸어보세요!", remainingSteps);
        } else if (remainingSteps < 5000) {
            return String.format("목표까지 %,d걸음 남았어요. 산책하기 좋은 날씨네요!", remainingSteps);
        } else {
            return String.format("목표까지 %,d걸음 남았어요. 오늘도 건강한 하루 보내세요!", remainingSteps);
        }
    }
}