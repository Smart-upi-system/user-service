package com.uws.user_service.service;

import com.uws.user_service.dto.UpdateKycRequest;
import com.uws.user_service.dto.UpdateProfileRequest;
import com.uws.user_service.dto.UserProfileResponse;
import com.uws.user_service.dto.ValidationResponse;

public interface UserProfileService {

   UserProfileResponse getUserProfile(String userId);

   UserProfileResponse updateUserProfile(String userId , UpdateProfileRequest profileRequest);

   UserProfileResponse updateKycStatus(String userId, UpdateKycRequest kycRequest);

   ValidationResponse validateUpiId(String upiId);

   UserProfileResponse getUserByUpiId(String upiId);

   ValidationResponse validateUser(String userId);
}
