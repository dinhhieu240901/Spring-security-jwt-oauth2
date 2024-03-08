package com.codegym.service.impl;

import com.codegym.model.entity.Token;
import com.codegym.repository.TokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class TokenService {
    @Autowired
    private TokenRepo tokenRepo;

    public List<Token> getTokenByUserIdAndTokenType(long userId, String tokenType) {
        return tokenRepo.getByUser_IdAndType_Name(userId, tokenType);
    }

    public boolean isTokenValid(String email, String code) {
        Optional<Token> tokenOptional = tokenRepo.findByCode(code);
        if (tokenOptional.isPresent()) {
            Token token = tokenOptional.get();
            return token.getUser().getEmail().equals(email) && token.getExpiredTime().getTime() > System.currentTimeMillis() && token.isStatus();
        }
        return false;
    }

    public boolean add(Token token) {
        try {
            token.setCreatedTime(new Timestamp(System.currentTimeMillis()));
            token.setExpiredTime(new Timestamp(token.getCreatedTime().getTime() + 10 * 60 * 1000));
            tokenRepo.save(token);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean disableTokenByType(long userId, String type) {
        List<Token> tokens = getTokenByUserIdAndTokenType(userId, type);
        if (!tokens.isEmpty()) {
            for (Token token : tokens) {
                token.setStatus(false);
            }
        }
        return true;
    }

    @Scheduled(fixedRate = 5000)
    public void clearToken() {
        tokenRepo.findAll().forEach(token -> {
            if (token.getExpiredTime().getTime() <= System.currentTimeMillis()) {
                token.setStatus(false);
                //Delete???
            }
        });
    }
}
