package com.example.chat.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.chat.exception.CustomException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenUtil {

    /***************************************************************************************/

    public String getTokenFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("Authorization");
            // System.out.println("token : " + token);
            if (token != null && token.startsWith("Bearer "))
                return token.substring(7);
        }
        return null;
    }

    /***************************************************************************************/

    @Value("${auth.secret}")
    private String TOKEN_SECRET;

    public String generateToken(String userName, Integer userId, Integer TOKEN_VALIDITY) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("userName", userName);
        claims.put("userId", userId);
        claims.put("created", new Date());


        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate(TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET).compact();
    }

    /***************************************************************************************/
    public String getUserName(String token) {
        if (token == null)
            return null;

            Claims claims = getClaims(token);


            return claims.get("userName").toString();

    }

    /***************************************************************************************/
    public Integer getUserIdFromToken(String token) {
        if (token == null)
            return null;


        try {
            Claims claims = getClaims(token);
            Integer userId = (Integer) claims.get("userId");

            return userId;
        } catch (Exception ex) {
            log.error("Error getting user ID from token: {}", ex.getMessage());
            return null;
        }
    }
    /***************************************************************************************/
    public Integer getUserId() {
        String token = getTokenFromRequest();
        if (token == null)
            return null;

        try {
            Claims claims = getClaims(token);
            Integer userId = (Integer) claims.get("userId");

            return userId;
        } catch (Exception ex) {
            log.error("Error getting user ID from token: {}", ex.getMessage());
            return null;
        }
    }

    /***************************************************************************************/

    private Date generateExpirationDate(Integer TOKEN_VALIDITY) {
        return new Date(System.currentTimeMillis() + TOKEN_VALIDITY.longValue() * 1000);
    }

    /***************************************************************************************/

    public boolean isTokenValid(String token, UserDetails userDetails) {

        String username = getUserName(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /***************************************************************************************/
    public boolean isTokenExpired(String token) {
        try {

            return getClaims(token).getExpiration().before(new Date());
        } catch (Exception ex) {
            return true;
        }

    }

    /***************************************************************************************/
    private Claims getClaims(String token) {
        try {

            // System.out.println("Token: " + token);
            return Jwts.parser()
                    .setSigningKey(TOKEN_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } 
        catch (ExpiredJwtException e) {
            // Log the exception and return claims even if the token is expired
            return e.getClaims();
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            // Log the exception and handle other token parsing issues

            throw new CustomException(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

