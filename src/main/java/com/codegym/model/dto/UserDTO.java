package com.codegym.model.dto;

import com.codegym.model.entity.Provider;
import com.codegym.model.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Set<Role> roles;
    private Set<Provider> providers;
    private boolean enable;

}