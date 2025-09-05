package com.greenmate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/esg")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ESGController {

    @GetMapping("/dashboard")
    public ResponseEntity<?> getESGDashboard() {
        
        log.info("ESG 대시보드 조회");
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // 개인 ESG 현황
        Map<String, Object> personal = new HashMap<>();
        personal.put("totalPoints", 1250);
        personal.put("monthlyPoints", 180);
        personal.put("weeklyPoints", 45);
        personal.put("rank", 15);
        personal.put("level", "Green Hero");
        personal.put("nextLevelPoints", 350);
        
        // 환경 기여 통계
        Map<String, Object> environmental = new HashMap<>();
        environmental.put("carbonReduced", 15.6); // kg CO2
        environmental.put("treesPlanted", 3); // 나무 심기 효과
        environmental.put("energySaved", 2.8); // kWh
        environmental.put("distanceWalked", 45.2); // km (이번 달)
        
        // 사회적 기여
        Map<String, Object> social = new HashMap<>();
        social.put("charityChallenges", 2);
        social.put("communityEvents", 1);
        social.put("volunteering", 0);
        
        dashboard.put("personal", personal);
        dashboard.put("environmental", environmental);
        dashboard.put("social", social);
        
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/challenges")
    public ResponseEntity<?> getAvailableChallenges() {
        
        log.info("사용 가능한 ESG 챌린지 조회");
        
        List<Map<String, Object>> challenges = Arrays.asList(
            createChallenge("주간 도보 챌린지", "이번 주에 10km 걷기", 100, 7, "ACTIVE"),
            createChallenge("대중교통 이용 챌린지", "자가용 대신 대중교통 5회 이용", 150, 14, "AVAILABLE"),
            createChallenge("지역 정화 활동", "동네 쓰레기 줍기 참여", 200, 30, "AVAILABLE"),
            createChallenge("에너지 절약 챌린지", "전기 사용량 10% 줄이기", 120, 21, "COMPLETED")
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("challenges", challenges);
        response.put("totalAvailable", challenges.size());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/challenges/{challengeId}/join")
    public ResponseEntity<?> joinChallenge(@PathVariable String challengeId) {
        
        log.info("ESG 챌린지 참여: challengeId={}", challengeId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("challengeId", challengeId);
        result.put("status", "JOINED");
        result.put("joinedAt", LocalDateTime.now());
        result.put("progress", 0);
        result.put("message", "챌린지에 성공적으로 참여했습니다!");
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard(
            @RequestParam(defaultValue = "weekly") String period) {
        
        log.info("리더보드 조회: period={}", period);
        
        List<Map<String, Object>> leaderboard = Arrays.asList(
            createLeaderboardEntry(1, "EcoWarrior", 340, "Green Champion"),
            createLeaderboardEntry(2, "NatureGuide", 285, "Green Hero"),
            createLeaderboardEntry(3, "CleanAir", 258, "Green Hero"),
            createLeaderboardEntry(4, "WalkMaster", 242, "Green Explorer"),
            createLeaderboardEntry(5, "EcoFriend", 210, "Green Explorer")
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("leaderboard", leaderboard);
        response.put("period", period);
        response.put("myRank", 15);
        response.put("myPoints", 180);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rewards")
    public ResponseEntity<?> getAvailableRewards() {
        
        log.info("사용 가능한 리워드 조회");
        
        List<Map<String, Object>> rewards = Arrays.asList(
            createReward("스타벅스 아메리카노", 500, "coffee", true),
            createReward("지하철 무료 이용권", 300, "transport", true),
            createReward("친환경 텀블러", 800, "eco-product", false),
            createReward("나무 심기 후원", 1000, "donation", false),
            createReward("자전거 대여 쿠폰", 200, "bike", true)
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("rewards", rewards);
        response.put("userPoints", 1250);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rewards/{rewardId}/redeem")
    public ResponseEntity<?> redeemReward(@PathVariable String rewardId) {
        
        log.info("리워드 교환: rewardId={}", rewardId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("rewardId", rewardId);
        result.put("status", "REDEEMED");
        result.put("redeemedAt", LocalDateTime.now());
        result.put("couponCode", "GMTE" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        result.put("expiresAt", LocalDate.now().plusDays(30));
        result.put("message", "리워드가 성공적으로 교환되었습니다!");
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/impact-report")
    public ResponseEntity<?> getPersonalImpactReport() {
        
        log.info("개인 환경 영향 리포트 조회");
        
        Map<String, Object> report = new HashMap<>();
        
        // 월별 통계
        List<Map<String, Object>> monthlyData = Arrays.asList(
            Map.of("month", "2025-09", "distance", 45.2, "co2Reduced", 15.6, "points", 180),
            Map.of("month", "2025-08", "distance", 38.7, "co2Reduced", 13.2, "points", 155),
            Map.of("month", "2025-07", "distance", 52.1, "co2Reduced", 17.9, "points", 208)
        );
        
        // 누적 영향
        Map<String, Object> totalImpact = new HashMap<>();
        totalImpact.put("totalDistance", 312.8);
        totalImpact.put("totalCO2Reduced", 89.2);
        totalImpact.put("equivalentTrees", 18);
        totalImpact.put("energySaved", 156.4);
        totalImpact.put("totalPoints", 1250);
        
        report.put("monthlyData", monthlyData);
        report.put("totalImpact", totalImpact);
        report.put("generatedAt", LocalDateTime.now());
        
        return ResponseEntity.ok(report);
    }

    private Map<String, Object> createChallenge(String name, String description, int points, int days, String status) {
        Map<String, Object> challenge = new HashMap<>();
        challenge.put("id", UUID.randomUUID().toString());
        challenge.put("name", name);
        challenge.put("description", description);
        challenge.put("points", points);
        challenge.put("duration", days);
        challenge.put("status", status);
        challenge.put("participants", (int)(Math.random() * 500) + 50);
        return challenge;
    }

    private Map<String, Object> createLeaderboardEntry(int rank, String username, int points, String level) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("rank", rank);
        entry.put("username", username);
        entry.put("points", points);
        entry.put("level", level);
        entry.put("avatar", "https://api.dicebear.com/7.x/avataaars/svg?seed=" + username);
        return entry;
    }

    private Map<String, Object> createReward(String name, int cost, String category, boolean available) {
        Map<String, Object> reward = new HashMap<>();
        reward.put("id", UUID.randomUUID().toString());
        reward.put("name", name);
        reward.put("cost", cost);
        reward.put("category", category);
        reward.put("available", available);
        reward.put("description", name + " 교환 쿠폰");
        return reward;
    }
}