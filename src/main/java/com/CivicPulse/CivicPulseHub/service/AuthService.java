package com.CivicPulse.CivicPulseHub.service;

import com.CivicPulse.CivicPulseHub.dto.RegisterRequest;
import com.CivicPulse.CivicPulseHub.entity.User;
import com.CivicPulse.CivicPulseHub.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Temporary OTP storage (email -> otp)
    private final Map<String, String> otpStorage = new HashMap<>();

    // ================= REGISTER =================
    public String register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username already exists";
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists";
        }

        if (userRepository.existsByGovernmentId(request.getGovernmentId())) {
            return "Government ID already exists";
        }

        String role = determineRole(request.getGovernmentId());

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .governmentId(request.getGovernmentId())
                .role(role)
                .enabled(true)
                .build();

        userRepository.save(user);

        return "Registration successful as " + role;
    }

    // ================= LOGIN =================
    // ================= LOGIN =================
    public String login(String username, String password, String governmentId) {

        Optional<User> userOptional =
                userRepository.findByUsernameAndGovernmentId(
                        username.trim(),
                        governmentId.trim()
                );

        if (userOptional.isEmpty()) {
            return "Invalid credentials";
        }

        User user = userOptional.get();

        boolean match = passwordEncoder.matches(
                password.trim(),
                user.getPassword()
        );

        System.out.println("Password Match: " + match);

        if (!match) {
            return "Invalid credentials";
        }

        return "Login successful as " + user.getRole();
    }  // ================= ROLE DETECTION =================
    private String determineRole(String governmentId) {

        if (governmentId == null || governmentId.isBlank()) {
            throw new RuntimeException("Government ID cannot be empty");
        }

        String id = governmentId.toLowerCase();

        if (id.startsWith("citizen")) {
            return "CITIZEN";
        }

        if (id.startsWith("officer")) {
            return "OFFICER";
        }

        if (id.startsWith("admin")) {
            return "ADMIN";
        }

        throw new RuntimeException("Invalid Government ID prefix");
    }

    // ================= SEND OTP =================
    public String sendOtp(String email) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "Email not found";
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        otpStorage.put(email, otp);

        // Send OTP email
        emailService.sendOtpEmail(email, otp);

        return "OTP sent successfully";
    }

    // ================= VERIFY OTP =================
    public String verifyOtp(String email, String otp) {

        String storedOtp = otpStorage.get(email);

        if (storedOtp == null) {
            return "OTP expired or not generated";
        }

        if (!storedOtp.equals(otp)) {
            return "Invalid OTP";
        }

        otpStorage.remove(email);

        return "OTP verified successfully";
    }

    // ================= RESET PASSWORD =================
    public String resetPassword(String email, String newPassword) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "User not found";
        }

        User user = userOptional.get();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password updated successfully";
    }
}