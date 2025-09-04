package com.greenmate.controller;

import com.greenmate.dto.WalkRecordRequest;
import com.greenmate.entity.User;
import com.greenmate.entity.WalkRecord;
import com.greenmate.service.WalkRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/walk-records")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WalkRecordController {
    
    private final WalkRecordService walkRecordService;
    
    @PostMapping
    public ResponseEntity<?> saveWalkRecord(@Valid @RequestBody WalkRecordRequest request) {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        
        WalkRecord savedRecord = walkRecordService.saveWalkRecord(request, user);
        return ResponseEntity.ok(savedRecord);
    }
    
    @GetMapping
    public ResponseEntity<List<WalkRecord>> getAllWalkRecords() {
        List<WalkRecord> records = walkRecordService.getAllWalkRecords();
        return ResponseEntity.ok(records);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WalkRecord> getWalkRecordById(@PathVariable Long id) {
        return walkRecordService.getWalkRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<WalkRecord>> getWalkRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<WalkRecord> records = walkRecordService.getWalkRecordsByDateRange(startDate, endDate);
        return ResponseEntity.ok(records);
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<?> getWalkStatistics() {
        // 전체 통계는 관리자만 접근 가능하도록 변경
        // 개별 사용자 통계는 /api/users/walk-statistics 사용
        User user = getCurrentUser();
        if (user == null || user.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(403).body("Admin access required");
        }
        
        WalkRecordService.WalkStatistics statistics = walkRecordService.getWalkStatistics();
        return ResponseEntity.ok(statistics);
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