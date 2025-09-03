package com.diamco.v1.settings;

import com.diamco.v1.filter.JwtFilter;
import com.diamco.v1.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
                // ⚠️ ORDRE IMPORTANT: CORS AVANT authorizeHttpRequests
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // 🔥 CRITIQUE: OPTIONS doit être EN PREMIER et AVANT tout autre matcher
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        
                        // Routes publiques
                        .requestMatchers("/api/auth/**").permitAll() 
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/test").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/test").permitAll()

                        // Routes authentifiées
                        // .requestMatchers("/api/auth/logout").authenticated()
                        // .requestMatchers("/api/client/**").hasAnyRole("CLIENT", "ADMIN", "SUPERADMIN")
                        // .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
                        // .requestMatchers("/api/superadmin/**").hasRole("SUPERADMIN")
                        // .requestMatchers("/api/technicien/**").hasAnyRole("TECHNICIEN", "ADMIN", "SUPERADMIN")

                        // Tout le reste nécessite une authentification
                        .anyRequest().authenticated())
                // 🔥 Appliquer JwtFilter seulement sur les routes protégées
                .addFilterBefore(new JwtFilter(jwtUtils, customUserDetailsService), UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }

    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration configuration = new CorsConfiguration();
        
    //     // ✅ Permet toutes les origines pour le développement
    //     configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    //     // configuration.setAllowedOrigins(Arrays.asList("http://localhost:8081", "exp://172.17.4.82:8081", "http://172.17.4.82:8082/api"));

    //     // ✅ Toutes les méthodes HTTP
    //     configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        
    //     // ✅ Tous les headers
    //     configuration.setAllowedHeaders(Arrays.asList("*"));
        
    //     // ✅ Permet les credentials (important pour JWT)
    //     configuration.setAllowCredentials(true);
        
    //     // ✅ Headers exposés dans les réponses
    //     configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
    //     // ✅ Cache les requêtes preflight pendant 1 heure
    //     configuration.setMaxAge(3600L);
        
    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", configuration);
    //     return source;
    // }




    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 🔥 En dev, accepte tout
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}