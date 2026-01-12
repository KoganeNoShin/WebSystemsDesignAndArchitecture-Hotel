package com.websystemdesign.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest != null) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            
            if (role.equals("ROLE_AMMINISTRATORE")) {
                getRedirectStrategy().sendRedirect(request, response, "/admin/dashboard");
                return;
            } else if (role.equals("ROLE_STAFF")) {
                getRedirectStrategy().sendRedirect(request, response, "/staff/dashboard");
                return;
            } else if (role.equals("ROLE_CLIENTE")) {
                getRedirectStrategy().sendRedirect(request, response, "/cliente/dashboard");
                return;
            }
        }

        getRedirectStrategy().sendRedirect(request, response, "/");
    }
}
