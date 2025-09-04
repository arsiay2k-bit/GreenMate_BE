package com.greenmate.security;

import com.greenmate.entity.User;
import com.greenmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final UserRepository userRepository;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        UserInfo userInfo = getUserInfo(registrationId, attributes);
        User user = saveOrUpdateUser(userInfo);
        
        return new CustomOAuth2User(oAuth2User.getAuthorities(), attributes, user);
    }
    
    private UserInfo getUserInfo(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return UserInfo.builder()
                    .email((String) attributes.get("email"))
                    .name((String) attributes.get("name"))
                    .picture((String) attributes.get("picture"))
                    .provider("google")
                    .providerId((String) attributes.get("sub"))
                    .build();
        }
        throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
    }
    
    private User saveOrUpdateUser(UserInfo userInfo) {
        Optional<User> existingUser = userRepository.findByProviderAndProviderId(
                userInfo.getProvider(), userInfo.getProviderId());
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(userInfo.getName());
            user.setPicture(userInfo.getPicture());
            return userRepository.save(user);
        } else {
            User newUser = User.builder()
                    .email(userInfo.getEmail())
                    .name(userInfo.getName())
                    .picture(userInfo.getPicture())
                    .provider(userInfo.getProvider())
                    .providerId(userInfo.getProviderId())
                    .role(User.Role.USER)
                    .build();
            return userRepository.save(newUser);
        }
    }
    
    @lombok.Builder
    @lombok.Data
    public static class UserInfo {
        private String email;
        private String name;
        private String picture;
        private String provider;
        private String providerId;
    }
}