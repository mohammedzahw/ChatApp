package com.example.chat.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class Validator {
    public static Response validate(BindingResult result) {

        Map<String, String> errors = new HashMap<>();
        errors = result.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        return new Response(HttpStatus.BAD_REQUEST, errors, "Validation Failed");
    }

}
