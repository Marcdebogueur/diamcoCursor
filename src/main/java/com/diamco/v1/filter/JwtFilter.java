package com.diamco.v1.filter;

import com.diamco.v1.entities.Utilisateur;
import com.diamco.v1.repository.UtilisateurRepository;
import com.diamco.v1.services.CustomUserDetailsService;
import com.diamco.v1.settings.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        String nom = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                nom = jwtUtils.extractUserName(jwt);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                String expiredSubject = e.getClaims().getSubject();
                if (expiredSubject != null) {
                    Utilisateur user = utilisateurRepository.findByEmail(expiredSubject);
                    if (user != null && user.isAuthentificated()) {
                        user.setAuthentificated(false);
                        utilisateurRepository.save(user);
                    }
                }
            }
        }

        if (nom != null && SecurityContextHolder.getContext().getAuthentication() == null ) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(nom);

            if (jwtUtils.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
