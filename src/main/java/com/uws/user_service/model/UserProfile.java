package com.uws.user_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_profiles", indexes = {
        @Index(name = "idx_upi_id", columnList = "upiId", unique = true),
        @Index(name = "idx_user_id", columnList = "userId"),
        @Index(name = "idx_kyc_status", columnList = "kycStatus")
})
public class UserProfile {

    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false,length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String upiID;

    @Column(length = 15)
    private String phone;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String kycStatus = "PENDING"; // PENDING, VERIFIED, REJECTED

    @Column(length = 500)
    private String kycDocumentUrl;

    @Column(length = 500)
    private String profilePictureUrl;

    @Column(length = 500)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 10)
    private String pincode;

    @Column(length = 36)
    private String walletId; // Reference to main wallet (updated by WalletCreated event)

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
