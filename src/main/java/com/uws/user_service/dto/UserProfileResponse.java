package com.uws.user_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {
    private String userId;
    private String firstName;
    private String lastName;
    private String upiID;
    private String phone;
    private String kycStatus; // PENDING, VERIFIED, REJECTED
    private String kycDocumentUrl;
    private String profilePictureUrl;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String walletId; // Reference to main wallet (updated by WalletCreated event)
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
