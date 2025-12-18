package com.websystemdesign.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            
            if (role.equals("ROLE_AMMINISTRATORE")) {
                response.sendRedirect("/admin/dashboard");
                return;
            } else if (role.equals("ROLE_STAFF")) {
                response.sendRedirect("/staff/dashboard");
                return;
            } else if (role.equals("ROLE_CLIENTE")) {
                response.sendRedirect("/cliente/dashboard");
                return;
            }
        }
        
        // Fallback per sicurezza, anche se non dovrebbe mai accadere
        response.sendRedirect("/");
    }
}
