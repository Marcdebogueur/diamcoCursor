package com.diamco.v1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${alertes.dir:src/main/resources/static/assets/alertes}")
    private String alertesDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configuration pour servir les fichiers d'alertes
        String uploadPath = Paths.get(alertesDir).toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/assets/alertes/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(3600); // Cache de 1 heure
    }
}