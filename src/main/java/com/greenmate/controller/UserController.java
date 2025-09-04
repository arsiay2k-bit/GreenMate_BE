package com.greenmate.controller;

import com.greenmate.entity.User;
import com.greenmate.entity.WalkRecord;
import com.greenmate.repository.WalkRecordRepository;
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
    
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("email", user.getEmail());
        profile.put("name", user.getName());
        profile.put("picture", user.getPicture());
        profile.put("provider", user.getProvider());
        profile.put("createdAt", user.getCreatedAt());
        
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
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        
        return (User) authentication.getPrincipal();
    }
}