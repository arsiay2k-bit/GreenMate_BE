package com.greenmate.service;

import com.greenmate.dto.SignUpRequest;
import com.greenmate.dto.StepRecommendationRequest;
import com.greenmate.dto.StepRecommendationResponse;
import com.greenmate.entity.User;
import com.greenmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AiAgentService aiAgentService;
    
    public User createUser(SignUpRequest signUpRequest) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다");
        }
        
        // 닉네임 중복 체크
        if (userRepository.existsByNickname(signUpRequest.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다");
        }
        
        // 패스워드 일치 확인
        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다");
        }
        
        // 사용자 생성
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .nickname(signUpRequest.getNickname())
                .gender(signUpRequest.getGender())
                .age(signUpRequest.getAge())
                .height(signUpRequest.getHeight())
                .weight(signUpRequest.getWeight())
                .role(User.Role.USER)
                .build();
        
        // 사용자 저장
        User savedUser = userRepository.save(user);
        
        // AI 에이전트로부터 개인맞춤형 걸음 수 추천 받기
        updateStepRecommendations(savedUser);
        
        return savedUser;
    }
    
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
    }
    
    @Transactional(readOnly = true)
    public boolean isValidPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 사용자의 개인맞춤형 걸음 수 추천 정보를 업데이트합니다.
     */
    public void updateStepRecommendations(User user) {
        try {
            log.info("사용자 {}의 걸음 수 추천 정보 업데이트 시작", user.getEmail());
            
            // AI 에이전트 요청 생성
            StepRecommendationRequest request = StepRecommendationRequest.fromUser(user);
            
            // AI 에이전트 호출
            StepRecommendationResponse recommendation = aiAgentService.getStepRecommendation(request);
            
            // 추천 정보를 사용자에게 저장
            user.setDailyStepsRecommendation(recommendation.getDailySteps());
            user.setDailyCaloriesBurnRecommendation(recommendation.getDailyCaloriesBurn());
            user.setDailyWalkingTimeRecommendation(recommendation.getDailyWalkingTimeMinutes());
            user.setPersonalizedRecommendations(recommendation.getPersonalizedAdvice());
            user.setRecommendationsUpdatedAt(LocalDateTime.now());
            
            userRepository.save(user);
            
            log.info("사용자 {}의 걸음 수 추천 정보 업데이트 완료: {} 걸음, {} kcal, {} 분", 
                    user.getEmail(), 
                    recommendation.getDailySteps(), 
                    recommendation.getDailyCaloriesBurn(), 
                    recommendation.getDailyWalkingTimeMinutes());
                    
        } catch (Exception e) {
            log.error("사용자 {}의 걸음 수 추천 정보 업데이트 실패: {}", user.getEmail(), e.getMessage(), e);
        }
    }
    
    /**
     * 사용자의 걸음 수 추천 정보를 수동으로 갱신합니다.
     */
    public User refreshStepRecommendations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        updateStepRecommendations(user);
        return user;
    }
}