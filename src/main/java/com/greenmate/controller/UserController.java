package com.greenmate.controller;

import com.greenmate.entity.User;
import com.greenmate.entity.WalkRecord;
import com.greenmate.repository.WalkRecordRepository;
import com.greenmate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final WalkRecordRepository walkRecordRepository;
    private final UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("email", user.getEmail());
        profile.put("nickname", user.getNickname());
        profile.put("gender", user.getGender());
        profile.put("age", user.getAge());
        profile.put("height", user.getHeight());
        profile.put("weight", user.getWeight());
        profile.put("role", user.getRole());
        profile.put("createdAt", user.getCreatedAt());
        
        // 개인맞춤형 걸음 수 추천 정보 추가
        Map<String, Object> recommendations = new HashMap<>();
        recommendations.put("dailySteps", user.getDailyStepsRecommendation());
        recommendations.put("dailyCaloriesBurn", user.getDailyCaloriesBurnRecommendation());
        recommendations.put("dailyWalkingTimeMinutes", user.getDailyWalkingTimeRecommendation());
        recommendations.put("personalizedAdvice", user.getPersonalizedRecommendations());
        recommendations.put("updatedAt", user.getRecommendationsUpdatedAt());
        profile.put("stepRecommendations", recommendations);
        
        return ResponseEntity.ok(profile);
    }
    
    @GetMapping("/walk-records")
    public ResponseEntity<List<WalkRecord>> getUserWalkRecords() {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        List<WalkRecord> records = walkRecordRepository.findByUserOrderByCreatedAtDesc(user);
        return ResponseEntity.ok(records);
    }
    
    @GetMapping("/walk-statistics")
    public ResponseEntity<?> getUserWalkStatistics() {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        Double totalDistance = walkRecordRepository.getTotalDistanceByUser(user);
        Long totalSteps = walkRecordRepository.getTotalStepsByUser(user);
        Long totalWalkCount = walkRecordRepository.countByUser(user);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalDistance", totalDistance != null ? totalDistance : 0.0);
        statistics.put("totalSteps", totalSteps != null ? totalSteps : 0L);
        statistics.put("totalWalkCount", totalWalkCount);
        statistics.put("averageDistance", totalWalkCount > 0 && totalDistance != null ? 
            totalDistance / totalWalkCount : 0.0);
        
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/walk-records/recent")
    public ResponseEntity<?> getRecentWalkRecords(@RequestParam(defaultValue = "7") int days) {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<WalkRecord> records = walkRecordRepository.findByUserAndCreatedAtAfterOrderByCreatedAtDesc(
            user, startDate);
        
        return ResponseEntity.ok(records);
    }
    
    @PostMapping("/refresh-recommendations")
    public ResponseEntity<?> refreshStepRecommendations() {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        try {
            User updatedUser = userService.refreshStepRecommendations(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "걸음 수 추천 정보가 업데이트되었습니다.");
            response.put("dailySteps", updatedUser.getDailyStepsRecommendation());
            response.put("dailyCaloriesBurn", updatedUser.getDailyCaloriesBurnRecommendation());
            response.put("dailyWalkingTimeMinutes", updatedUser.getDailyWalkingTimeRecommendation());
            response.put("personalizedAdvice", updatedUser.getPersonalizedRecommendations());
            response.put("updatedAt", updatedUser.getRecommendationsUpdatedAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("추천 정보 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @GetMapping("/recommendations")
    public ResponseEntity<?> getStepRecommendations() {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        Map<String, Object> recommendations = new HashMap<>();
        recommendations.put("dailySteps", user.getDailyStepsRecommendation());
        recommendations.put("dailyCaloriesBurn", user.getDailyCaloriesBurnRecommendation());
        recommendations.put("dailyWalkingTimeMinutes", user.getDailyWalkingTimeRecommendation());
        recommendations.put("personalizedAdvice", user.getPersonalizedRecommendations());
        recommendations.put("updatedAt", user.getRecommendationsUpdatedAt());
        
        return ResponseEntity.ok(recommendations);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        
        return (User) authentication.getPrincipal();
    }
}