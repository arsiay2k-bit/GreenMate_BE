package com.greenmate.controller;

import com.greenmate.dto.AuthResponse;
import com.greenmate.dto.LoginRequest;
import com.greenmate.dto.SignUpRequest;
import com.greenmate.entity.User;
import com.greenmate.service.UserService;
import com.greenmate.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AuthController {
    
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest signUpRequest) {
        try {
            User user = userService.createUser(signUpRequest);
            String token = jwtUtil.generateJwtToken(user.getEmail(), user.getId());
            AuthResponse response = AuthResponse.from(token, user);
            
            log.info("새 사용자 가입: {}", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("회원가입 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "회원가입 중 오류가 발생했습니다"));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.findByEmail(loginRequest.getEmail());
            
            if (!userService.isValidPassword(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "이메일 또는 패스워드가 일치하지 않습니다"));
            }
            
            String token = jwtUtil.generateJwtToken(user.getEmail(), user.getId());
            AuthResponse response = AuthResponse.from(token, user);
            
            log.info("사용자 로그인: {}", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "이메일 또는 패스워드가 일치하지 않습니다"));
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "로그인 중 오류가 발생했습니다"));
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        User user = (User) authentication.getPrincipal();
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("nickname", user.getNickname());
        response.put("gender", user.getGender());
        response.put("age", user.getAge());
        response.put("role", user.getRole());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("Invalid token format");
        }
        
        String token = authHeader.substring(7);
        
        if (!jwtUtil.validateJwtToken(token)) {
            return ResponseEntity.status(401).body("Invalid token");
        }
        
        String email = jwtUtil.getEmailFromJwtToken(token);
        Long userId = jwtUtil.getUserIdFromJwtToken(token);
        
        String newToken = jwtUtil.generateJwtToken(email, userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newToken);
        response.put("tokenType", "Bearer");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT는 stateless이므로 클라이언트에서 토큰을 제거하면 됨
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully logged out");
        
        return ResponseEntity.ok(response);
    }
}