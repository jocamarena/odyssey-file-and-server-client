package com.example.odyssey.fileandserver.controller;

import com.example.odyssey.fileandserver.dto.*;
import com.example.odyssey.fileandserver.service.EfmUserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/efm/user")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "efm.enabled", havingValue = "true", matchIfMissing = true)
public class EfmUserController {

    private final EfmUserServiceClient efmUserServiceClient;

    @PostMapping("/authenticate")
    public ResponseEntity<EfmResponse<Map<String, Object>>> authenticateUser(
            @RequestBody AuthenticateUserRequest request) {
        log.info("REST request to authenticate user: {}", request.getEmail());
        EfmResponse<Map<String, Object>> response = efmUserServiceClient.authenticateUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<EfmResponse<Map<String, Object>>> getUser(
            @PathVariable("userId") String userId) {
        log.info("REST request to get user: {}", userId);
        GetUserRequest request = new GetUserRequest();
        request.setUserID(userId);
        EfmResponse<Map<String, Object>> response = efmUserServiceClient.getUser(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<EfmResponse<Map<String, Object>>> updateUser(
            @PathVariable("userId") String userId,
            @RequestBody UpdateUserRequest request) {
        log.info("REST request to update user: {}", userId);
        request.setUserID(userId);
        EfmResponse<Map<String, Object>> response = efmUserServiceClient.updateUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<EfmResponse<Map<String, Object>>> changePassword(
            @RequestBody ChangePasswordRequest request) {
        log.info("REST request to change password for user: {}", request.getEmail());
        EfmResponse<Map<String, Object>> response = efmUserServiceClient.changePassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<EfmResponse<Map<String, Object>>> resetPassword(
            @RequestBody ResetPasswordRequest request) {
        log.info("REST request to reset password for user: {}", request.getEmail());
        EfmResponse<Map<String, Object>> response = efmUserServiceClient.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/password-question")
    public ResponseEntity<EfmResponse<Map<String, Object>>> getPasswordQuestion(
            @RequestParam("email") String email) {
        log.info("REST request to get password question for: {}", email);
        GetPasswordQuestionRequest request = new GetPasswordQuestionRequest();
        request.setEmail(email);
        EfmResponse<Map<String, Object>> response = efmUserServiceClient.getPasswordQuestion(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/notification-preferences")
    public ResponseEntity<EfmResponse<Map<String, Object>>> getNotificationPreferences(
            @PathVariable("userId") String userId) {
        log.info("REST request to get notification preferences for user: {}", userId);
        GetNotificationPreferencesRequest request = new GetNotificationPreferencesRequest();
        request.setUserID(userId);
        EfmResponse<Map<String, Object>> response = efmUserServiceClient.getNotificationPreferences(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/notification-preferences")
    public ResponseEntity<EfmResponse<Map<String, Object>>> updateNotificationPreferences(
            @PathVariable("userId") String userId,
            @RequestBody UpdateNotificationPreferencesRequest request) {
        log.info("REST request to update notification preferences for user: {}", userId);
        request.setUserID(userId);
        EfmResponse<Map<String, Object>> response = efmUserServiceClient.updateNotificationPreferences(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-activation-email")
    public ResponseEntity<EfmResponse<Map<String, Object>>> selfResendActivationEmail(
            @RequestBody SelfResendActivationEmailRequest request) {
        log.info("REST request to resend activation email for: {}", request.getEmail());
        EfmResponse<Map<String, Object>> response = efmUserServiceClient.selfResendActivationEmail(request);
        return ResponseEntity.ok(response);
    }
}
