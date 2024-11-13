package com.example.chat.controllers;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.dtos.SignUpRequest;
import com.example.chat.exception.CustomException;
import com.example.chat.models.LocalUser;
import com.example.chat.repositories.LocalUserRepository;
import com.example.chat.security.TokenUtil;
import com.example.chat.services.SignUpService;
import com.example.chat.shared.Response;
import com.example.chat.shared.Validator;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController

@RequestMapping("api/")
public class SignUpController {

    private SignUpService signUpService;

    private LocalUserRepository localUserRepository;

    private TokenUtil tokenUtil;

    public SignUpController(SignUpService signUpService, LocalUserRepository localUserRepository, TokenUtil tokenUtil) {
        this.signUpService = signUpService;
        this.localUserRepository = localUserRepository;
        this.tokenUtil = tokenUtil;
    }

    /******************************************************************************************************************/

    @PostMapping(value = "/signup")

    public Response signUp(@Valid @ModelAttribute SignUpRequest signUpRequest, BindingResult result,
            HttpServletRequest request) throws MessagingException, IOException, SQLException {
        try {

            if (result.hasErrors()) {
                return Validator.validate(result);
            }
            LocalUser LocalUser = localUserRepository.findByEmail(signUpRequest.getEmail()).orElse(null);
            if (LocalUser != null) {
                return new Response(HttpStatus.BAD_REQUEST, null, "Email already exists ,Please login");
            }

            LocalUser = signUpService.saveUser(signUpRequest);

            // send email
            String token = tokenUtil.generateToken(signUpRequest.getEmail(),
                    LocalUser.getId(), 1000);
            signUpService.sendRegistrationVerificationCode(signUpRequest.getEmail(),
                    request,
                    token);

            return new Response(HttpStatus.OK, null, "Activation link sent to your email");
        } catch (CustomException e) {
            return new Response(HttpStatus.BAD_REQUEST, null, e.getMessage());

        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, null, e.getMessage());
        }

    }

    /******************************************************************************************************************/

    @GetMapping("/verifyEmail/{token}")
    public ResponseEntity<?> verifyEmail(@PathVariable("token") String verficationToken,
            HttpServletResponse response)
            throws SQLException, IOException {

        signUpService.verifyEmail(verficationToken);

        return new ResponseEntity<>("Account is verified, you can login now", HttpStatus.OK);
    }

}