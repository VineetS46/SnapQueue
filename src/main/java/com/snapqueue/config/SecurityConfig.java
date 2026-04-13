package com.snapqueue.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snapqueue.service.EmployeeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;

// all Spring Security setup is here — what's public, what needs login, how passwords work
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // @Lazy is needed here to break a circular dependency
    // SecurityConfig needs EmployeeService, EmployeeService needs PasswordEncoder,
    // PasswordEncoder is defined in SecurityConfig — circular
    // @Lazy tells Spring to wait and only create EmployeeService when it's first used
    private final EmployeeService employeeService;

    public SecurityConfig(@Lazy EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // bcrypt is the standard for password hashing — slow by design to resist brute force
    // this bean gets injected into EmployeeService for hashing and into the auth provider for checking
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // tells Spring Security to use our DB (via EmployeeService) to look up users
    // and bcrypt to verify passwords — this is the standard setup for DB-backed auth
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(employeeService); // our custom loadUserByUsername
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // we need this as a bean so AuthController can call authenticate() directly
    // without it we'd have to use form-based login which doesn't work for a REST API
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // the main security rules — what's allowed, what's blocked, what happens on 401
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        ObjectMapper mapper = new ObjectMapper(); // for writing JSON in the 401 response

        http
            // CSRF off — we're a REST API, not a traditional form-based app
            // CSRF protection is for browser forms that use cookies, which we do use,
            // but our login is handled via JS fetch so it's fine to disable
            .csrf(csrf -> csrf.disable())

            // allow iframes — not strictly needed but avoids issues if we ever embed pages
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",   // login, register, logout — must be public
                    "/login.html",
                    "/register.html",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()
                .anyRequest().authenticated() // everything else needs a valid session
            )

            // instead of redirecting to /login when unauthenticated (Spring's default),
            // return a JSON 401 — the frontend handles the redirect to login.html
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    mapper.writeValue(response.getWriter(),
                            Map.of("message", "Unauthorized - please log in"));
                })
            )

            // one active session per user — if they log in somewhere else, old session dies
            .sessionManagement(session -> session
                .maximumSessions(1)
            );

        return http.build();
    }
}
