package com.example.chat.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.chat.dtos.SignUpRequest;
import com.example.chat.exception.CustomException;
import com.example.chat.mappers.LocalUserMapper;
import com.example.chat.models.LocalUser;
import com.example.chat.models.Role;
import com.example.chat.repositories.LocalUserRepository;
import com.example.chat.security.TokenUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Service

public class SignUpService {
    private LocalUserMapper localUserMapper;
    private EmailService emailService;
    private RoleService roleService;

    private PasswordEncoder passwordEncoder;
    private LocalUserRepository localUserRepository;
    private TokenUtil tokenUtil;
    private ImageService imageService;

    public SignUpService(EmailService emailService, RoleService roleService,
            PasswordEncoder passwordEncoder, TokenUtil tokenUtil,
            ImageService imageService,
            LocalUserRepository localUserRepository,
            LocalUserMapper localUserMapper) {
        this.imageService = imageService;
        this.localUserRepository = localUserRepository;

        this.localUserMapper = localUserMapper;
        this.emailService = emailService;

        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.tokenUtil = tokenUtil;
    }

    /******************************************************************************************************************/

    public LocalUser saveUser(SignUpRequest request)
            throws MessagingException, IOException, SQLException {
        try {

            request.setPassword(passwordEncoder.encode(request.getPassword()));
            LocalUser user = localUserMapper.toEntity(request);

            Role role = roleService.getByRole("ROLE_USER");
            if (role == null) {
                role = new Role();
                role.setRole("ROLE_USER");

                roleService.saveRole(role);
            }
            user.setRoles(List.of(role));
            if (request.getImage() != null)
                user = imageService.uploadImage(request.getImage(), user);

            localUserRepository.save(user);

            return user;

        } catch (CustomException e) {

            throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    // /******************************************************************************************************************/

    public void verifyEmail(String token) throws SQLException, IOException {

        if (tokenUtil.isTokenExpired(token)) {
            throw new CustomException("Token is expired", HttpStatus.BAD_REQUEST);
        }
        // String email = tokenUtil.getUserIdFromToken(token)
        LocalUser User = localUserRepository.findByEmail(tokenUtil.getUserName(token))
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        User.setActive(true);
        localUserRepository.save(User);

        // return ResponseEntity.ok("Account is verified, you can login now!");
    }

    /******************************************************************************************************************/
    public void sendRegistrationVerificationCode(String email, HttpServletRequest request,
            String verficationToken) {
        try {
            String url = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()
                    + "/api/verifyEmail/" + verficationToken;

            System.out.println("url : " + url);
            String subject = "Email Verification";
            String senderName = "User Registration Portal Service";
            String content = "<p> Hi, " + email + ", </p>" +
                    "<p>Thank you for registering with us," + "" +
                    "Please, follow the link below to complete your registration.</p>" +
                    "<a href=\"" + url + "\">Verify your email to activate your account</a>" +
                    "<p> Thank you <br> Users Registration Portal Service";
            emailService.sendEmail(email, content, subject, senderName);

        } catch (Exception e) {
            throw new CustomException("Error while sending Email", HttpStatus.BAD_REQUEST);
        }
    }

    /**************************************************************************************************************/

}
