package com.codegym.repository;

import com.codegym.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM User where username= :username or email = :username or phone = :username", nativeQuery = true)
    Optional<User> getUserByUsernameOrEmailOrPhone(@Param("username") String username);

    Optional<User> findByUsernameOrEmailOrPhone(String username, String email, String phone);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

}
