package com.CivicPulse.CivicPulseHub.controllers;

import com.CivicPulse.CivicPulseHub.dto.LoginRequest;
import com.CivicPulse.CivicPulseHub.dto.RegisterRequest;
import com.CivicPulse.CivicPulseHub.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    // ================= REGISTER =================
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");
        String governmentId = request.get("governmentId");

        return ResponseEntity.ok(authService.login(username, password, governmentId));
    }
    // ================= SEND OTP =================
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email) {
        return authService.sendOtp(email);
    }

    // ================= VERIFY OTP =================
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp) {
        return authService.verifyOtp(email, otp);
    }

    // ================= RESET PASSWORD =================
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request.getEmail(), request.getNewPassword());
    }

    // ================= REQUEST DTO FOR RESET PASSWORD =================
    @Data
    static class ResetPasswordRequest {
        private String email;
        private String newPassword;
    }
}