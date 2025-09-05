package com.greenmate.dto;

import com.greenmate.entity.User;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignUpRequest {
    
    @NotBlank(message = "이메일은 필수 입력 항목입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    @NotBlank(message = "패스워드는 필수 입력 항목입니다")
    @Size(min = 8, message = "패스워드는 최소 8자 이상이어야 합니다")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
             message = "패스워드는 대소문자, 숫자, 특수문자를 포함해야 합니다")
    private String password;
    
    @NotBlank(message = "패스워드 확인은 필수 입력 항목입니다")
    private String confirmPassword;
    
    @NotBlank(message = "닉네임은 필수 입력 항목입니다")
    @Size(min = 2, max = 20, message = "닉네임은 2-20자 사이여야 합니다")
    private String nickname;
    
    @NotNull(message = "성별은 필수 선택 항목입니다")
    private User.Gender gender;
    
    @NotNull(message = "나이는 필수 입력 항목입니다")
    @Min(value = 14, message = "만 14세 이상만 가입 가능합니다")
    @Max(value = 120, message = "올바른 나이를 입력해주세요")
    private Integer age;
}