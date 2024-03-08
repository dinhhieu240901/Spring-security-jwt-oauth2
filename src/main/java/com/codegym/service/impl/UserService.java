package com.codegym.service.impl;

import com.codegym.model.entity.Provider;
import com.codegym.model.entity.User;
import com.codegym.model.UserPrinciple;
import com.codegym.model.dto.UserDTO;
import com.codegym.repository.UserRepo;
import com.codegym.service.IUserService;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.util.*;

@Service
@CacheConfig(cacheNames = "users")
public class UserService implements UserDetailsService, IUserService {

    @Autowired
    private UserRepo userRepo;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);


    private ModelMapper modelMapper = new ModelMapper();


    public void updateAuthenticationType(String username, String oauth2ClientName) {
        Provider authType = new Provider(oauth2ClientName.toUpperCase());
//        repo.updateAuthenticationType(username, authType);
        userRepo.findAll().forEach(user -> {
            if (user.getUsername().equals(username)) {
                Set<Provider> providers = new HashSet<>();
                providers.add(authType);
                user.setProviders(providers);
                System.out.println("Updated user's authentication type to " + authType);
            }
        });

    }

    public boolean addO2AuthAccount(String username, String provider) {
        try {
            Set<Provider> providers = new HashSet<>();
            providers.add(new Provider(provider.toUpperCase()));
            User user = User.builder().username(username).providers(providers).enable(true).build();
            userRepo.save(user);
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }

    }

    @Override
    public boolean updateUserPass(String username, String newPass) {
        try {
            User user = findByUsername_v02(username);
            if (user != null) {
                user.setPassword(passwordEncoder.encode(newPass));
                userRepo.save(user);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error during updating user: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Cacheable(key = "'allUsers'")
    public List<UserDTO> findAll() {
        Type targetListType = new TypeToken<List<UserDTO>>() {
        }.getType();
        return modelMapper.map(userRepo.findAll(), targetListType);

    }


    @Override
    @Cacheable(key = "#id")
    public Optional<UserDTO> findById(Long id) {
        Optional<User> userOptional = userRepo.findById(id);
        return userOptional.map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        Optional<User> userOptional = userRepo.getUserByUsernameOrEmailOrPhone(username);
        return Optional.ofNullable(modelMapper.map(userOptional.get(), UserDTO.class));
    }

    public User findByUsername_v02(String username) {
        return userRepo.getUserByUsernameOrEmailOrPhone(username).orElse(null);
    }

    @Override
    public boolean add(UserDTO userDTO) {
        if (!(userRepo.findByUsernameOrEmailOrPhone(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPhone()).isPresent())) {
            User user = User.builder().username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .email(userDTO.getEmail())
                    .phone(userDTO.getEmail()).build();
            userRepo.save(user);
            return true;
        }
        return false;
    }

    public void processOAuthPostLogin(String username) {
        userRepo.findAll().forEach(user -> {
            if (!user.getUsername().equals(username)) {
                User newUser = new User();
                newUser.setUsername(username);
                Set<Provider> providers = new HashSet<>();
                providers.add(new Provider("GOOGLE"));
                user.setProviders(providers);
                newUser.setProviders(providers);
                newUser.setEnable(true);
                System.out.println("Created new user: " + username);
            }
        });
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    public void delete(Long id) {
        userRepo.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> userOptional = userRepo.getUserByUsernameOrEmailOrPhone(username);
        System.out.println("load user success");
        return userOptional.map(UserPrinciple::build).orElse(null);
    }

    public UserDTO toDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public boolean checkValidUser(String username) {
        return loadUserByUsername(username) != null;
    }

    public boolean checkUserExisted(String type, String username) {
        Optional<User> user = Optional.empty();
        switch (type.toUpperCase()) {
            case "USERNAME":
                user = userRepo.findByUsername(username);
                break;
            case "EMAIL":
                user = userRepo.findByEmail(username);
                break;
            case "PHONE":
                user = userRepo.findByPhone(username);
                break;
        }
        return user.isPresent();
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Scheduled(fixedRate = 50000)
    @CacheEvict(value = "users", allEntries = true)
    public void clearAllCache() {
        System.out.println("Clear all caches");
    }

}