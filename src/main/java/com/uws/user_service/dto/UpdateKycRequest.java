package com.uws.user_service.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateKycRequest {

    @NotBlank(message = "KYC status is required")
    @Pattern(regexp = "^(PENDING|VERIFIED|REJECTED)$",
            message = "KYC status must be PENDING, VERIFIED, or REJECTED")
    private String kycStatus; // PENDING, VERIFIED, REJECTED

    private String kycDocumentUrl;

}
