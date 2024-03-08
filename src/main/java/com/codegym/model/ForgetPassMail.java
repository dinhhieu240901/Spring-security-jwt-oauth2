package com.codegym.model;



import com.codegym.model.entity.Token;
import com.codegym.utils.DigitsGenerator;
import lombok.Data;


@Data

public class ForgetPassMail extends MailMessage{
    private Token token;



    public ForgetPassMail() {
        super();
        setSubject("Token: Code for reset password");
        token = Token.builder().code(DigitsGenerator.generate(6))
                .status(true)
                .build();
        setContent(token.getCode());
    }
}
