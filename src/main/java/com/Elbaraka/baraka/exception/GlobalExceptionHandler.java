package com.Elbaraka.baraka.exception;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiErrorResponse>  builResponse(HttpStatus status, String message, HttpServletRequest request){
        ApiErrorResponse response=new ApiErrorResponse(
                LocalDateTime.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response,status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> resourceNotFound(ResourceNotFoundException ex,HttpServletRequest request){
        return builResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusnisseException(BusinessException ex,HttpServletRequest request)
    {
        return builResponse(HttpStatus.CONFLICT,ex.getMessage(),request);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex,HttpServletRequest request)
    {
        return  builResponse(HttpStatus.CONFLICT,ex.getMessage(),request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedEcxection(AccessDeniedException ex,HttpServletRequest request){
        return builResponse(HttpStatus.FORBIDDEN,"Access Denied: You do not have the required role. ",request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException ex,HttpServletRequest request){
        return builResponse(HttpStatus.UNAUTHORIZED,"your authentification faild "+ex.getMessage(),request);
    }



}
