package com.uws.user_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidationResponse {

    private boolean exists;
    private boolean active;
    private boolean kycVerified;
    private String message;

}
