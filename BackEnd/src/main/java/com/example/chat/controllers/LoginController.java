package com.example.chat.controllers;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.dtos.ChangePasswordRequest;
import com.example.chat.dtos.LoginRequest;
import com.example.chat.exception.CustomException;
import com.example.chat.models.LocalUser;
import com.example.chat.repositories.LocalUserRepository;
import com.example.chat.security.TokenUtil;
import com.example.chat.services.LoginService;
import com.example.chat.services.SignUpService;
import com.example.chat.shared.Response;
import com.example.chat.shared.Validator;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController

@RequestMapping("api/")
public class LoginController {

    private SignUpService signUpService;

    private LocalUserRepository localUserRepository;

    private TokenUtil tokenUtil;

    private LoginService loginService;

    public LoginController(SignUpService signUpService, LocalUserRepository localUserRepository, TokenUtil tokenUtil,
            LoginService loginService) {
        this.signUpService = signUpService;
        this.localUserRepository = localUserRepository;
        this.tokenUtil = tokenUtil;
        this.loginService = loginService;
    }

    /***************************************************************************************************************/
    @PostMapping("/login/custom")
    public Response loginWithEmailAndPassword(@RequestBody @Valid LoginRequest loginRequest, BindingResult result,
            HttpServletRequest request)
            throws MessagingException, SQLException, IOException {
        try {
            if (result.hasErrors()) {
                return Validator.validate(result);
            }
            return new Response(HttpStatus.OK, loginService.verifyLogin(loginRequest, request), "Login Successfull");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    /****************************************************************************************************************/

    @PostMapping("/forget-password")

    public Response enterEmailToChangePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest,
            BindingResult result,
            HttpServletRequest request)
            throws MessagingException, SQLException, IOException {

        if (result.hasErrors()) {
            return Validator.validate(result);
        }

        LocalUser LocalUser = localUserRepository.findByEmail(changePasswordRequest.getEmail()).orElseThrow(
                () -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        if (!LocalUser.getActive()) {
            signUpService.sendRegistrationVerificationCode(changePasswordRequest.getEmail(), request,
                    tokenUtil.generateToken(changePasswordRequest.getEmail(), LocalUser.getId(), 900));

            return new Response(HttpStatus.OK, null, "This email is not verified, Activation link sent to your email");
        }
        loginService.sendResetpasswordEmail(changePasswordRequest.getEmail(), request,
                tokenUtil.generateToken(changePasswordRequest.getEmail() + "," + changePasswordRequest.getPassword(),
                        LocalUser.getId(), 900));

        return new Response(HttpStatus.OK, null, "Password reset link sent to your email");
    }

    /*************************************************************************************************************/
    @SuppressWarnings("unused")
    @GetMapping("/check-token/{token}")
    public ResponseEntity<?> savePassword(@PathVariable("token") String token, HttpServletResponse response)
            throws SQLException, IOException, MessagingException {

        String email = tokenUtil.getUserName(token).split(",")[0];

        String password = tokenUtil.getUserName(token).split(",")[1];

        LocalUser LocalUser = localUserRepository.findByEmail(email).orElseThrow(
                () -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (tokenUtil.isTokenExpired(token)) {
            throw new CustomException("Token is expired", HttpStatus.BAD_REQUEST);
        }
        loginService.savePassword(email, password);

        return new ResponseEntity<>("Password Changed Successfully", HttpStatus.OK);
    }

}
