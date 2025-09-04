package com.greenmate.controller;

import com.greenmate.entity.User;
import com.greenmate.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final JwtUtil jwtUtil;
    
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
        response.put("name", user.getName());
        response.put("picture", user.getPicture());
        response.put("provider", user.getProvider());
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