package com.codegym.service;

import com.codegym.model.entity.User;
import com.codegym.model.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<UserDTO> findAll();

    Optional<UserDTO> findById(Long id);

    Optional<UserDTO> findByUsername(String username);

    boolean add(UserDTO userDTO);

    void delete(Long id);
    boolean addO2AuthAccount(String username, String provider);
    User findByUsername_v02(String username);
     boolean updateUserPass(String username, String newPass);
    Optional<User> findByEmail(String email);
    boolean checkUserExisted(String type, String username);
}
