package com.diamco.v1.settings;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        log.error(" Accès refusé [403] - Méthode: {} | URI: {} | Raison: {}",
                method, uri, accessDeniedException.getMessage());

        response.sendError(HttpServletResponse.SC_FORBIDDEN,
                "Accès refusé : " + accessDeniedException.getMessage());
    }
}
