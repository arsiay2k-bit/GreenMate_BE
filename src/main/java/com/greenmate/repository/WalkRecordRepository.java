package com.greenmate.repository;

import com.greenmate.entity.WalkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalkRecordRepository extends JpaRepository<WalkRecord, Long> {
    
    @Query("SELECT w FROM WalkRecord w WHERE w.createdAt >= :startDate AND w.createdAt <= :endDate ORDER BY w.createdAt DESC")
    List<WalkRecord> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(w.distanceKm) FROM WalkRecord w")
    Double getTotalDistance();
    
    @Query("SELECT SUM(w.steps) FROM WalkRecord w")
    Long getTotalSteps();
    
    @Query("SELECT COUNT(w) FROM WalkRecord w")
    Long getTotalWalkCount();
}