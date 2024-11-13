// package com.example.e_commerce.exception;

// import org.aspectj.lang.annotation.AfterThrowing;
// import org.aspectj.lang.annotation.Aspect;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Component;

// @Aspect
// @Component
// public class ExceptionAspect {

//     // private final Logger log = LoggerFactory.getLogger(ExceptionAspect.class);

//     @AfterThrowing(value  = "execution(* com.example.e_commerce.controller..*(..))", throwing = "ex")
//     public void handleExceptions(Exception ex) {
//         // logErrorBasedOnExceptionType(ex);
//         // System.out.println("Exception occurred: " );
//         try {
//             throw ex;
//         } catch (Exception e) {
//             // log.error("Exception occurred: {}", e.getMessage());
//             System.out.println("Exception occurred: " + e.getMessage());
//         }
//         // return new ResponseEntity<>("asss", HttpStatus.BAD_REQUEST);
//     }

//     // private void logErrorBasedOnExceptionType(Exception ex) {
//     // if (ex instanceof IOException ioEx) {
//     // log.error("IOException occurred: {}", ioEx.getMessage());
//     // } else if (ex instanceof SQLException sqlEx) {
//     // log.error("SQLException occurred: {}", sqlEx.getMessage());
//     // } else {
//     // log.error("Exception occurred: {}", ex.getMessage());
//     // }
//     // }
// }