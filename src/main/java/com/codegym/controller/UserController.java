package com.codegym.controller;

import com.codegym.model.CustomOAuth2User;
import com.codegym.jwt.service.JwtResponse;
import com.codegym.model.LoginRequest;
import com.codegym.jwt.service.JwtService;
import com.codegym.model.dto.UserDTO;
import com.codegym.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")

public class UserController {

    @Autowired

    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    String jwt;

    @Autowired
    private AuthenticationSuccessHandler successHandler;

    /* ---------------- GET ALL USER ------------------------ */
//    @RequestMapping(value = "/users", method = RequestMethod.GET)
//    @Cacheable(value = "users", key = "allUsers")
//    public ResponseEntity<List<UserDTO>> getAllUser() {
//        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
//    }

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.findAll();
    }

    /* ---------------- GET USER BY ID ------------------------ */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public @ResponseBody UserDTO getUserById(@PathVariable Long id) {
        Optional<UserDTO> user = userService.findById(id);
        return user.orElse(null);
    }

    /* ---------------- CREATE NEW USER ------------------------ */
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody UserDTO user) {
        if (userService.add(user)) {
            return new ResponseEntity<>("Created!", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("User Existed!", HttpStatus.BAD_REQUEST);
        }
    }

    /* ---------------- DELETE USER ------------------------ */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)

    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        userService.delete(id);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody LoginRequest user, HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            jwt = jwtService.generateTokenLogin(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserDTO userInfo = userService.findByUsername(user.getUsername()).get();
//            successHandler.onAuthenticationSuccess(request, response, authentication);
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userInfo.getUsername(), userInfo.getUsername(), userDetails.getAuthorities()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/o2auth/success")
    public ResponseEntity<?> o2auth() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("authen: " + authentication);
            CustomOAuth2User userInfo = (CustomOAuth2User) authentication.getPrincipal();
            jwt = jwtService.generateTokenLogin(authentication);
            return ResponseEntity.ok(new JwtResponse(jwt, userInfo.getEmail(), userInfo.getName(), userInfo.getAuthorities()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    @GetMapping("/accesstoken")
    public String getAccessTokenValue() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomOAuth2User) {
                CustomOAuth2User oauthToken = (CustomOAuth2User) authentication.getPrincipal();
                for (Map.Entry<String, Object> entry : oauthToken.getAttributes().entrySet()) {
                    System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue().toString());
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }


}