package com.diamco.v1.settings;

import com.diamco.v1.filter.JwtFilter;
import com.diamco.v1.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // accessible à tout le monde
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()

                        //.requestMatchers("/api/auth/logout").authenticated()

                        // accessible uniquement aux CLIENT
                        //.requestMatchers("/api/client/**").hasAnyRole("CLIENT", "ADMIN", "SUPERADMIN")

                        // accessible uniquement aux ADMIN
                        //.requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")

                        // accessible uniquement aux SUPERADMIN
                        //.requestMatchers("/api/superadmin/**").hasRole("SUPERADMIN")

                        // accessible uniquement aux TECHNICIEN
                        //.requestMatchers("/api/technicien/**").hasAnyRole("TECHNICIEN")

                        // toutes les autres routes nécessitent une authentification
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtFilter(jwtUtils, customUserDetailsService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
