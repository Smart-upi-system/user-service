package com.uws.user_service.controller;

import com.uws.user_service.dto.UpdateKycRequest;
import com.uws.user_service.dto.UpdateProfileRequest;
import com.uws.user_service.dto.UserProfileResponse;
import com.uws.user_service.dto.ValidationResponse;
import com.uws.user_service.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@RequestHeader("X-User-Id")  String userId){
        log.info("GET /users/profile - userId: {}", userId);
        UserProfileResponse userProfileResponse=userProfileService.getUserProfile(userId);

        return ResponseEntity.ok(userProfileResponse);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestHeader("X-User-Id")  String userId, @Valid @RequestBody UpdateProfileRequest request){
        log.info("PUT /users/profile - userId: {}", userId);

        UserProfileResponse userProfileResponse=userProfileService.updateUserProfile(userId,request);
        return ResponseEntity.ok(userProfileResponse);
    }

    @PutMapping("/update-kyc")
    public ResponseEntity<UserProfileResponse> updateKyc(@RequestHeader("X-User-Id")  String userId, @Valid @RequestBody UpdateKycRequest request){
        log.info("PUT /users/kyc - userId: {}, status: {}", userId, request.getKycStatus());

        UserProfileResponse userProfileResponse=userProfileService.updateKycStatus(userId,request);

        return ResponseEntity.ok(userProfileResponse);
    }

    @GetMapping("/validation/{upiId}")
    public ResponseEntity<ValidationResponse> validation(@PathVariable String upiId){
        log.info("GET /users/validate/{} - validating UPI ID", upiId);
        ValidationResponse validationResponse=userProfileService.validateId(upiId);

        return ResponseEntity.ok(validationResponse);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Service is running");
    }


}
