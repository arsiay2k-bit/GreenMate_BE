package com.greenmate.service;

import com.greenmate.dto.WalkRecordRequest;
import com.greenmate.entity.WalkRecord;
import com.greenmate.repository.WalkRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class WalkRecordService {
    
    private final WalkRecordRepository walkRecordRepository;
    
    public WalkRecord saveWalkRecord(WalkRecordRequest request, com.greenmate.entity.User user) {
        WalkRecord walkRecord = WalkRecord.builder()
                .user(user)
                .startLatitude(request.getStartLatitude())
                .startLongitude(request.getStartLongitude())
                .startAddress(request.getStartAddress())
                .endLatitude(request.getEndLatitude())
                .endLongitude(request.getEndLongitude())
                .endAddress(request.getEndAddress())
                .distanceKm(request.getDistanceKm())
                .durationMinutes(request.getDurationMinutes())
                .steps(request.getSteps())
                .routeType(request.getRouteType())
                .build();
        
        return walkRecordRepository.save(walkRecord);
    }
    
    @Transactional(readOnly = true)
    public List<WalkRecord> getAllWalkRecords() {
        return walkRecordRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<WalkRecord> getWalkRecordById(Long id) {
        return walkRecordRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<WalkRecord> getWalkRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return walkRecordRepository.findByDateRange(startDate, endDate);
    }
    
    @Transactional(readOnly = true)
    public WalkStatistics getWalkStatistics() {
        Double totalDistance = Optional.ofNullable(walkRecordRepository.getTotalDistance()).orElse(0.0);
        Long totalSteps = Optional.ofNullable(walkRecordRepository.getTotalSteps()).orElse(0L);
        Long totalWalkCount = walkRecordRepository.getTotalWalkCount();
        
        return WalkStatistics.builder()
                .totalDistance(totalDistance)
                .totalSteps(totalSteps)
                .totalWalkCount(totalWalkCount)
                .averageDistance(totalWalkCount > 0 ? totalDistance / totalWalkCount : 0.0)
                .build();
    }
    
    public static class WalkStatistics {
        private Double totalDistance;
        private Long totalSteps;
        private Long totalWalkCount;
        private Double averageDistance;
        
        public static WalkStatisticsBuilder builder() {
            return new WalkStatisticsBuilder();
        }
        
        public static class WalkStatisticsBuilder {
            private Double totalDistance;
            private Long totalSteps;
            private Long totalWalkCount;
            private Double averageDistance;
            
            public WalkStatisticsBuilder totalDistance(Double totalDistance) {
                this.totalDistance = totalDistance;
                return this;
            }
            
            public WalkStatisticsBuilder totalSteps(Long totalSteps) {
                this.totalSteps = totalSteps;
                return this;
            }
            
            public WalkStatisticsBuilder totalWalkCount(Long totalWalkCount) {
                this.totalWalkCount = totalWalkCount;
                return this;
            }
            
            public WalkStatisticsBuilder averageDistance(Double averageDistance) {
                this.averageDistance = averageDistance;
                return this;
            }
            
            public WalkStatistics build() {
                WalkStatistics stats = new WalkStatistics();
                stats.totalDistance = this.totalDistance;
                stats.totalSteps = this.totalSteps;
                stats.totalWalkCount = this.totalWalkCount;
                stats.averageDistance = this.averageDistance;
                return stats;
            }
        }
        
        public Double getTotalDistance() { return totalDistance; }
        public Long getTotalSteps() { return totalSteps; }
        public Long getTotalWalkCount() { return totalWalkCount; }
        public Double getAverageDistance() { return averageDistance; }
    }
}