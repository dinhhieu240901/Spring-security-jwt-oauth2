package com.codegym.repository;

import com.codegym.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepo extends JpaRepository<Token, Long> {
    List<Token> getByUser_IdAndType_Name(long userId, String tokenType);

    Optional<Token> findByCode(String code);
}
