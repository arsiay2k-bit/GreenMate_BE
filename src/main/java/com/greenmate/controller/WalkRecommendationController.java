package com.greenmate.controller;

import com.greenmate.dto.WalkRecommendationRequest;
import com.greenmate.dto.WalkRecommendationResponse;
import com.greenmate.service.WalkRecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/walk-recommendation")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class WalkRecommendationController {
    
    private final WalkRecommendationService walkRecommendationService;
    
    @PostMapping
    public ResponseEntity<WalkRecommendationResponse> getWalkRecommendation(
            @Valid @RequestBody WalkRecommendationRequest request) {
        
        log.info("도보 경로 추천 요청: userId={}, startKeyword={}, endKeyword={}, todaySteps={}", 
                request.getUserId(), request.getStartKeyword(), request.getEndKeyword(), request.getTodaySteps());
        
        try {
            WalkRecommendationResponse response = walkRecommendationService.getWalkRecommendation(request);
            log.info("도보 경로 추천 완료: userId={}, routeOptions={}", 
                    request.getUserId(), response.getRouteOptions().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("도보 경로 추천 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}