package com.codegym.controller;

import com.codegym.model.ForgetPassMail;
import com.codegym.model.entity.Token;
import com.codegym.model.entity.User;
import com.codegym.repository.TokenTypeRepo;
import com.codegym.service.impl.MailService;
import com.codegym.service.impl.TokenService;
import com.codegym.service.impl.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;




@Controller
@RequestMapping("/forgetPass")
public class ForgetPassController {
    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenTypeRepo tokenTypeRepo;


    @PostMapping({"/sendCode", "/sendCodeAgain"})
    public ResponseEntity<?> sendCode(@RequestBody String email, HttpServletRequest request) {
        if (userService.checkValidUser(email)) {
            HttpSession session = request.getSession();
            session.setAttribute("emailForgetPass", email);
            User user = userService.findByUsername_v02(email);
            tokenService.disableTokenByType(user.getId(), "FORGET_PASSWORD");
            ForgetPassMail mail = new ForgetPassMail();
            Token token = mail.getToken();
            token.setUser(user);
            token.setType(tokenTypeRepo.findByName("FORGET_PASSWORD").get());
            tokenService.add(token);
            mailService.send(mail, email);
            if (mailService.getSendingStatus()) {
                return ResponseEntity.ok("Send code success" + mail.getContent());
            } else {
                return ResponseEntity.internalServerError().body("Can not send email");
            }
        }
        return ResponseEntity.badRequest().body("Email not sign in");
    }

    @PostMapping("/validateCode")
    public ResponseEntity<?> validateCode(@RequestBody String code, @SessionAttribute("emailForgetPass") String email) {
        if (tokenService.isTokenValid(email, code)) {
            return ResponseEntity.ok("Validate code success");
        } else {
            return ResponseEntity.badRequest().body("Validate code fail");
        }
    }

    @PostMapping("/resetPass")
    public ResponseEntity<?> resetPass(@RequestBody String newPass, @SessionAttribute("emailForgetPass") String email) {
            if (userService.updateUserPass(email, newPass)) {
                return ResponseEntity.ok("Reset pass success");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during update user");
            }
    }


    @ExceptionHandler(HttpSessionRequiredException.class)
    public ResponseEntity<String> handleSessionRequiredException(HttpSessionRequiredException ex) {
        return new ResponseEntity<>("Session attribute 'emailForgetPass' not found" , HttpStatus.BAD_REQUEST);
    }


}
