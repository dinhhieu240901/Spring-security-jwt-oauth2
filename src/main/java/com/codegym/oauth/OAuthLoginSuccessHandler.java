package com.codegym.oauth;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.codegym.model.CustomOAuth2User;
import com.codegym.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


@Component

public class OAuthLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    UserService userService;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String oauth2ClientName = oauth2User.getOauth2ClientName();
        String username = oauth2User.getEmail();
        if (userService.loadUserByUsername(username) != null) {
            System.out.println("login success");
            response.sendRedirect("/api/o2auth/success");
        } else {

            if (userService.addO2AuthAccount(username, oauth2ClientName)) {
                response.sendRedirect("/api/o2auth/success");
            } else {
                response.sendRedirect("/login?error");
            }

        }
        

    }


}