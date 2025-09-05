package com.greenmate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @NotBlank(message = "이메일은 필수 입력 항목입니다")
    private String email;
    
    @Column(nullable = false)
    @NotBlank(message = "패스워드는 필수 입력 항목입니다")
    private String password;
    
    @Column(nullable = false, length = 50)
    @NotBlank(message = "닉네임은 필수 입력 항목입니다")
    private String nickname;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "성별은 필수 선택 항목입니다")
    private Gender gender;
    
    @Column(nullable = false)
    @NotNull(message = "나이는 필수 입력 항목입니다")
    private Integer age;
    
    @Column(nullable = true)
    private Double height;
    
    @Column(nullable = true)
    private Double weight;
    
    @Column(nullable = true)
    private Integer dailyStepsRecommendation;
    
    @Column(nullable = true)
    private Double dailyCaloriesBurnRecommendation;
    
    @Column(nullable = true)
    private Integer dailyWalkingTimeRecommendation;
    
    @Column(nullable = true, length = 1000)
    private String personalizedRecommendations;
    
    @Column(nullable = true)
    private LocalDateTime recommendationsUpdatedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (role == null) {
            role = Role.USER;
        }
    }
    
    public enum Gender {
        MALE("남성"),
        FEMALE("여성"),
        OTHER("기타");
        
        private final String description;
        
        Gender(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum Role {
        USER, ADMIN
    }
}