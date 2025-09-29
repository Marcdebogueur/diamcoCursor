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
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        .requestMatchers("/api/auth/logout").authenticated()

                        // Dashboard réservé ADMIN et SUPERADMIN
                        .requestMatchers("/api/dash/**").hasAnyAuthority("ADMIN", "SUPERADMIN")

                        // accessible uniquement aux CLIENT
                        .requestMatchers("/api/client/**").hasAnyAuthority("CLIENT", "ADMIN", "SUPERADMIN")

                        // accessible uniquement aux ADMIN
                        .requestMatchers("/api/admin/**").hasAnyAuthority("ADMIN", "SUPERADMIN")

                        // accessible uniquement aux SUPERADMIN
                        .requestMatchers("/api/superadmin/**").hasAuthority("SUPERADMIN")

                        // accessible uniquement aux TECHNICIEN
                        .requestMatchers("/api/technicien/**").hasAnyAuthority("TECHNICIEN", "ADMIN", "SUPERADMIN")

                        .anyRequest().authenticated())
                .addFilterBefore(new JwtFilter(jwtUtils, customUserDetailsService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
