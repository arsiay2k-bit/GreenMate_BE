package com.greenmate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/walk")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class WalkNavigationController {

    @GetMapping("/nearby-routes")
    public ResponseEntity<?> getNearbyWalkingRoutes(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1000") int radius) {
        
        log.info("근처 도보 경로 검색: lat={}, lng={}, radius={}m", lat, lng, radius);
        
        // 가상의 근처 도보 경로 데이터
        List<Map<String, Object>> routes = Arrays.asList(
            createRoute("한강공원 산책로", lat + 0.001, lng + 0.001, "쉬움", 1.2, 15, "강변을 따라 걷는 편안한 산책로"),
            createRoute("남산 둘레길", lat + 0.002, lng - 0.001, "보통", 2.8, 35, "서울 시내를 조망하며 걷는 둘레길"),
            createRoute("청계천로", lat - 0.001, lng + 0.002, "쉬움", 1.8, 22, "도심 속 물길을 따라 걷는 평탄한 길")
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("routes", routes);
        response.put("center", Map.of("lat", lat, "lng", lng));
        response.put("radius", radius);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/route-details/{routeId}")
    public ResponseEntity<?> getRouteDetails(@PathVariable String routeId) {
        
        log.info("경로 상세 정보: routeId={}", routeId);
        
        Map<String, Object> routeDetails = new HashMap<>();
        routeDetails.put("id", routeId);
        routeDetails.put("name", "한강공원 산책로");
        routeDetails.put("distance", 1.2);
        routeDetails.put("estimatedTime", 15);
        routeDetails.put("difficulty", "쉬움");
        routeDetails.put("description", "강변을 따라 걷는 편안한 산책로입니다.");
        routeDetails.put("elevationGain", 5);
        routeDetails.put("surfaceType", "포장도로");
        
        // 경로 좌표점들
        List<Map<String, Double>> waypoints = Arrays.asList(
            Map.of("lat", 37.5663, "lng", 126.9997),
            Map.of("lat", 37.5673, "lng", 127.0007),
            Map.of("lat", 37.5683, "lng", 127.0017),
            Map.of("lat", 37.5693, "lng", 127.0027)
        );
        routeDetails.put("waypoints", waypoints);
        
        // 주변 시설
        List<Map<String, Object>> facilities = Arrays.asList(
            Map.of("type", "화장실", "name", "한강공원 화장실", "distance", 50),
            Map.of("type", "음수대", "name", "음수대", "distance", 120),
            Map.of("type", "벤치", "name", "휴식공간", "distance", 80)
        );
        routeDetails.put("facilities", facilities);
        
        return ResponseEntity.ok(routeDetails);
    }

    @PostMapping("/start-walking")
    public ResponseEntity<?> startWalking(@RequestBody Map<String, Object> request) {
        
        String routeId = (String) request.get("routeId");
        log.info("도보 시작: routeId={}", routeId);
        
        Map<String, Object> walkingSession = new HashMap<>();
        walkingSession.put("sessionId", UUID.randomUUID().toString());
        walkingSession.put("routeId", routeId);
        walkingSession.put("startTime", LocalDateTime.now());
        walkingSession.put("status", "ACTIVE");
        walkingSession.put("distance", 0.0);
        walkingSession.put("steps", 0);
        walkingSession.put("calories", 0);
        
        return ResponseEntity.ok(walkingSession);
    }

    @PostMapping("/update-progress")
    public ResponseEntity<?> updateWalkingProgress(@RequestBody Map<String, Object> request) {
        
        String sessionId = (String) request.get("sessionId");
        Double currentLat = (Double) request.get("lat");
        Double currentLng = (Double) request.get("lng");
        Integer steps = (Integer) request.get("steps");
        
        log.info("도보 진행상황 업데이트: sessionId={}, lat={}, lng={}, steps={}", 
                sessionId, currentLat, currentLng, steps);
        
        Map<String, Object> progress = new HashMap<>();
        progress.put("sessionId", sessionId);
        progress.put("currentPosition", Map.of("lat", currentLat, "lng", currentLng));
        progress.put("distanceWalked", 0.5); // 걸은 거리 (km)
        progress.put("steps", steps);
        progress.put("elapsedTime", 8); // 경과 시간 (분)
        progress.put("remainingDistance", 0.7); // 남은 거리 (km)
        progress.put("estimatedRemainingTime", 9); // 남은 예상 시간 (분)
        progress.put("calories", 25); // 소모 칼로리
        
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/complete-walking")
    public ResponseEntity<?> completeWalking(@RequestBody Map<String, Object> request) {
        
        String sessionId = (String) request.get("sessionId");
        log.info("도보 완료: sessionId={}", sessionId);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("sessionId", sessionId);
        summary.put("completedAt", LocalDateTime.now());
        summary.put("totalDistance", 1.2);
        summary.put("totalTime", 17); // 분
        summary.put("totalSteps", 1500);
        summary.put("totalCalories", 75);
        summary.put("averageSpeed", 4.2); // km/h
        summary.put("esgPoints", 10); // 환경 기여 점수
        
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/walking-history")
    public ResponseEntity<?> getWalkingHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("도보 기록 조회: page={}, size={}", page, size);
        
        List<Map<String, Object>> history = Arrays.asList(
            createWalkingRecord("한강공원 산책로", "2025-09-04", 1.2, 17, 1500, 75),
            createWalkingRecord("남산 둘레길", "2025-09-03", 2.8, 42, 3500, 180),
            createWalkingRecord("청계천로", "2025-09-02", 1.8, 25, 2200, 110)
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", history);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", history.size());
        
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createRoute(String name, double lat, double lng, 
                                          String difficulty, double distance, int time, String description) {
        Map<String, Object> route = new HashMap<>();
        route.put("id", UUID.randomUUID().toString());
        route.put("name", name);
        route.put("startLat", lat);
        route.put("startLng", lng);
        route.put("difficulty", difficulty);
        route.put("distance", distance);
        route.put("estimatedTime", time);
        route.put("description", description);
        route.put("rating", 4.2 + Math.random() * 0.8); // 4.2-5.0 점 사이
        route.put("reviewCount", (int)(Math.random() * 100) + 10);
        return route;
    }

    private Map<String, Object> createWalkingRecord(String routeName, String date, 
                                                  double distance, int time, int steps, int calories) {
        Map<String, Object> record = new HashMap<>();
        record.put("id", UUID.randomUUID().toString());
        record.put("routeName", routeName);
        record.put("date", date);
        record.put("distance", distance);
        record.put("time", time);
        record.put("steps", steps);
        record.put("calories", calories);
        record.put("esgPoints", (int)(distance * 8)); // 거리당 ESG 포인트
        return record;
    }
}