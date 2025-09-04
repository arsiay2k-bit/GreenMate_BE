package com.greenmate.security;

import com.greenmate.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User {
    
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;
    private final User user;
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public String getName() {
        return user.getName();
    }
    
    public String getEmail() {
        return user.getEmail();
    }
    
    public Long getUserId() {
        return user.getId();
    }
    
    public User getUser() {
        return user;
    }
}