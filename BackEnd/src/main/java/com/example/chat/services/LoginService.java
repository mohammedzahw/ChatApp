package com.example.chat.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.chat.dtos.LoginRequest;
import com.example.chat.exception.CustomException;
import com.example.chat.models.LocalUser;
import com.example.chat.repositories.LocalUserRepository;
import com.example.chat.security.TokenUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class LoginService {

    private LocalUserRepository localUserRepository;

    private PasswordEncoder passwordEncoder;

    private TokenUtil tokenUtil;

    private SignUpService signUpService;

    private EmailService emailService;

    public LoginService(LocalUserService localUserService, PasswordEncoder passwordEncoder, TokenUtil tokenUtil,
            LocalUserRepository localUserRepository,
            SignUpService signUpService, EmailService emailService) {
        this.localUserRepository = localUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenUtil = tokenUtil;
        this.signUpService = signUpService;
        this.emailService = emailService;
    }

    /********************************************************************************* */
    public Map<String, Object> verifyLogin(LoginRequest loginRequest, HttpServletRequest request)
            throws SQLException, IOException {

        LocalUser user = localUserRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException("Wrong Password!", HttpStatus.BAD_REQUEST);
        }
        if (!user.getActive()) {
            String token = tokenUtil.generateToken(loginRequest.getEmail(), user.getId(), 1000);
            signUpService.sendRegistrationVerificationCode(loginRequest.getEmail(),
                    request,
                    token);
            throw new CustomException("Please verify your email first, link sent to your email",
                    HttpStatus.BAD_REQUEST);
        }
        String token = tokenUtil.generateToken(loginRequest.getEmail(), user.getId(), 3000000);
        localUserRepository.save(user);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);

        return response;

    }

    /********************************************************************************************************************/
    public void savePassword(String email, String password)
            throws SQLException, IOException, MessagingException {
        LocalUser user = localUserRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        user.setPassword(passwordEncoder.encode(password)); // encoded password);

        localUserRepository.save(user);

    }

    /********************************************************************************************************************/

    public void sendResetpasswordEmail(String email, HttpServletRequest request, String token) {

        try {
            String url = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()
                    + "/api/check-token/" + token;
            System.out.println("url : " + url);
            String subject = "Reset Password Verification";
            String senderName = "User Registration Portal Service";
            String content = "<p> Hi, " + email + ", </p>" +
                    "<p>Thank you for registering with us," + "" +
                    "Please, follow the link below to complete your registration.</p>" +
                    "<a href=\"" + url + "\">Reset password</a>" +
                    "<p> Thank you <br> Reset Password Portal Service";
            emailService.sendEmail(email, content, subject, senderName);

            // return new ResponseEntity<>("Please, check your email to reset your
            // password", HttpStatus.OK);
        } catch (Exception e) {
            throw new CustomException("Error while sending email", HttpStatus.BAD_REQUEST);

        }
    }

    /********************************************************************************************************************/

}
