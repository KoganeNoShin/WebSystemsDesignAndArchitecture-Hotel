package com.websystemdesign.config;

import com.websystemdesign.service.UtenteService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UtenteService utenteService;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UtenteService utenteService, CustomAuthenticationSuccessHandler successHandler, PasswordEncoder passwordEncoder) {
        this.utenteService = utenteService;
        this.successHandler = successHandler;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(utenteService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/home/**", "/api/booking/**", "/api/availability/**", "/login", "/register", "/css/**", "/image/**", "/js/**", "/json/**").permitAll()
                        .requestMatchers("/api/cliente/**").hasRole("CLIENTE")
                        .requestMatchers("/cliente/**").hasRole("CLIENTE")
                        .requestMatchers("/admin/**").hasRole("AMMINISTRATORE")
                        .requestMatchers("/staff/**").hasRole("STAFF")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/home")
                        .permitAll()
                );
        return http.build();
    }
}
