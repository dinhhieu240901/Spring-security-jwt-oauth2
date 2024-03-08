package com.codegym.jwt;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc)
            throws IOException {
        System.out.println("Error: " + exc.toString());
        System.out.println(exc.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.sendRedirect("/api/error403");
    }
}