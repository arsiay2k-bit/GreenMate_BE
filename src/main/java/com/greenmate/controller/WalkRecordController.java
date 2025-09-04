package com.greenmate.controller;

import com.greenmate.dto.WalkRecordRequest;
import com.greenmate.entity.WalkRecord;
import com.greenmate.service.WalkRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<WalkRecord> saveWalkRecord(@Valid @RequestBody WalkRecordRequest request) {
        WalkRecord savedRecord = walkRecordService.saveWalkRecord(request);
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
    public ResponseEntity<WalkRecordService.WalkStatistics> getWalkStatistics() {
        WalkRecordService.WalkStatistics statistics = walkRecordService.getWalkStatistics();
        return ResponseEntity.ok(statistics);
    }
}