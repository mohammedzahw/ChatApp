package com.example.chat.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.chat.repositories.LocalUserRepository;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@SuppressWarnings("null")
public class SecurityConfig {

    private LocalUserRepository localUserRepository;

    public SecurityConfig(LocalUserRepository localUserRepository) {
        this.localUserRepository = localUserRepository;
    }

    /*****************************************************************************************************/

    @Bean
    public UserDetailsService userDetailsService() {

        return username -> localUserRepository.findByEmail(username)
                .map(LocalUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /*****************************************************************************************************/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /***************************************************************************************************** */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    /***************************************************************************************************** */
    @Bean
    TokenUtil tokenUtil() {
        return new TokenUtil();
    }

    @Bean
    AuthFilter authFilter() {
        return new AuthFilter(userDetailsService(), tokenUtil());
    }

    /***************************************************************************************************** */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/**")
                .permitAll()
                .anyRequest().hasAnyRole("USER", "ADMIN", "INSTRUCTOR"));
        // .addFilterAfter(authFilter(), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Access Denied");
                }));
        return http.build();
    }

    /***************************************************************************************************** */

}
