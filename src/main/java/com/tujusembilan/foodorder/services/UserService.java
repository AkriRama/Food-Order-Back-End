package com.tujusembilan.foodorder.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.tujusembilan.foodorder.dto.request.LoginRequest;
import com.tujusembilan.foodorder.dto.request.RegisterRequest;
import com.tujusembilan.foodorder.dto.response.LoginResponse;
import com.tujusembilan.foodorder.dto.response.MessageResponse;
import com.tujusembilan.foodorder.models.User;
import com.tujusembilan.foodorder.models.UserDetail;
import com.tujusembilan.foodorder.repositories.UserRepository;
import com.tujusembilan.foodorder.utils.jwt.JwtUtil;

import org.springframework.security.core.AuthenticationException;



import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    @Autowired
    private Validator validator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<MessageResponse> registerUser(RegisterRequest request) {
        try {
            Set<ConstraintViolation<RegisterRequest>> constraintViolations = validator.validate(request);
            if (!constraintViolations.isEmpty()) {
                ConstraintViolation<RegisterRequest> firstConstraintViolation = constraintViolations.iterator().next();
                String message = firstConstraintViolation.getMessage();
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse(message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }

            if (userRepository.existsByUsername(request.getUsername())) {
                String message = messageSource.getMessage("username.exists", null, Locale.getDefault());
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse(message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }

            if (!request.getRetypePassword().equals(request.getPassword())) {
                String message = messageSource.getMessage("retypePassword.not.match", null, Locale.getDefault());
                return ResponseEntity
                        .ok()
                        .body(new MessageResponse(message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }

            User user = User.builder()
                    .username(request.getUsername())
                    .fullname(request.getFullname())
                    .password(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()))
                    .isDeleted(false)
                    .build();
            userRepository.save(user);

            String message = messageSource.getMessage("register.successful", null, Locale.getDefault());
            String formatMessage = MessageFormat.format(message, request.getUsername());

            return ResponseEntity
                    .ok()
                    .body(new MessageResponse(formatMessage, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase()));

        } catch (Exception e) {
            log.error(null, e);
            String message = messageSource.getMessage("internal.error", null, Locale.getDefault());
            return ResponseEntity
                    .internalServerError()
                    .body(new MessageResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));


        }
    }

    public ResponseEntity<LoginResponse> loginUser(LoginRequest request) {
        try {
            Set<ConstraintViolation<LoginRequest>> constraintViolations = validator.validate(request);
            if (!constraintViolations.isEmpty()) {
                ConstraintViolation<LoginRequest> firstConstraintViolation = constraintViolations.iterator().next();
                String message = firstConstraintViolation.getMessage();
                return ResponseEntity
                        .badRequest()
                        .body(new LoginResponse(null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken(authentication);

            UserDetail userDetail = (UserDetail) authentication.getPrincipal();

            String message = messageSource.getMessage("login.successful", null, Locale.getDefault());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Token", jwt);

            return ResponseEntity
                    .ok()
                    .body(LoginResponse.builder()
                            .data(new LoginResponse.UserData(userDetail.getUserId(), jwt, "Bearer ", userDetail.getUsername()))
                            .message(message)
                            .statusCode( HttpStatus.OK.value())
                            .status(HttpStatus.OK.getReasonPhrase())
                            .build()
                    );

        } catch (AuthenticationException e) {
            String message = messageSource.getMessage("login.error", null, Locale.getDefault());
            return ResponseEntity
                    .badRequest()
                    .body(new LoginResponse(null, message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));
        } catch (Exception e) {
            log.error(null, e);
            String message = messageSource.getMessage("internal.error", null, Locale.getDefault());
            return ResponseEntity
                    .internalServerError()
                    .body(new LoginResponse(null, message, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
        }
    }
}
