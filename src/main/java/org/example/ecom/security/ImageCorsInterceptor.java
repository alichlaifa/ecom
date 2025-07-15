package org.example.ecom.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ImageCorsInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // Set CORS headers for images
        response.setHeader("Access-Control-Allow-Origin", "*"); // Cela autorise n'importe quelle origine (domaine) à accéder à la ressource.
        response.setHeader("Access-Control-Allow-Methods", "*"); // Autorise toutes les méthodes HTTP (GET, POST, PUT, DELETE, etc.) pour les requêtes CORS.
        response.setHeader("Access-Control-Allow-Headers", "*"); // Permet tous les headers personnalisés dans les requêtes envoyées depuis un autre domaine

        return true;
    }
}
