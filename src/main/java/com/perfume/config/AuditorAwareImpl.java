package com.perfume.config;

import com.perfume.entity.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;


public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = "system";
        try{
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        }catch (Exception e){
            e.printStackTrace();
        }

        return Optional.of(username);
    }
}
