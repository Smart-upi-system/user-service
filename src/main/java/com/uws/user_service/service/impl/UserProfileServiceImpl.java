package com.uws.user_service.service.impl;

import com.uws.user_service.config.ModelMapperConfig;
import com.uws.user_service.dto.UpdateKycRequest;
import com.uws.user_service.dto.UpdateProfileRequest;
import com.uws.user_service.dto.UserProfileResponse;
import com.uws.user_service.dto.ValidationResponse;
import com.uws.user_service.model.UserProfile;
import com.uws.user_service.repository.UserProfileRepository;
import com.uws.user_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final ModelMapper modelMapper;

    @Override
    @Cacheable(value = "userProfile", key = "#userId")
    public UserProfileResponse getUserProfile(String userId) {
        UserProfile userProfile=userProfileRepository.findByUserId(userId);
        log.info("Getting user profile for userId: {}", userId);
        try {
            if(ObjectUtils.isEmpty(userProfile)){
                throw new RuntimeException("no user present with this user id");
            }

            UserProfileResponse response=modelMapper.map(userProfile,UserProfileResponse.class);
            return response;


        } catch (RuntimeException e) {
            log.info("user profile for userId: {} does not exists", userId);

            throw new RuntimeException("no user present with this user id");

        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "userProfile", key = "#userId" )
    public UserProfileResponse updateUserProfile(String userId, UpdateProfileRequest profileRequest) {
        log.info("Updating profile for userId: {}", userId);
        UserProfile userProfile=userProfileRepository.findByUserId(userId);

        if(profileRequest.getFirstName()!=null){
            userProfile.setFirstName(profileRequest.getFirstName());
        }
        if(profileRequest.getLastName()!=null){
            userProfile.setLastName(profileRequest.getLastName());
        }
        if(profileRequest.getPhone()!=null){
            userProfile.setPhone(profileRequest.getPhone());
        }
        if(profileRequest.getAddress()!=null){
            userProfile.setAddress(profileRequest.getAddress());
        }
        if(profileRequest.getCity()!=null){
            userProfile.setCity(profileRequest.getCity());
        }
        if(profileRequest.getPincode()!=null){
            userProfile.setPincode(profileRequest.getPincode());
        }
        if(profileRequest.getState()!=null){
            userProfile.setState(profileRequest.getState());
        }
        if(profileRequest.getProfilePictureUrl()!=null){
            userProfile.setProfilePictureUrl(profileRequest.getProfilePictureUrl());
        }

        UserProfile saveProfile=userProfileRepository.save(userProfile);
        log.info("Profile updated successfully for userId: {}", userId);
        return modelMapper.map(saveProfile,UserProfileResponse.class);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userProfile", key = "#userId")
    public UserProfileResponse updateKycStatus(String userId, UpdateKycRequest kycRequest) {
        log.info("Updating kyc status for userId: {}", userId);
        UserProfile userProfile=userProfileRepository.findByUserId(userId);


        userProfile.setKycStatus(kycRequest.getKycStatus());

        if(kycRequest.getKycDocumentUrl()!=null){
            userProfile.setKycDocumentUrl(kycRequest.getKycDocumentUrl());
        }
        UserProfile saveProfile=userProfileRepository.save(userProfile);
        log.info("KYC status updated to {} for userId: {}", kycRequest.getKycStatus(), userId);

        return modelMapper.map(saveProfile,UserProfileResponse.class);
    }

    @Override
    @Cacheable(value = "upiValidation", key = "#upiId")
    public ValidationResponse validateId(String upiId) {
        log.info("Validating UPI ID: {}", upiId);
        UserProfile userProfile=userProfileRepository.findByUpiId(upiId);

        if (userProfile == null) {
            return ValidationResponse.builder()
                    .exists(false)
                    .active(false)
                    .kycVerified(false)
                    .message("UPI ID not found")
                    .build();
        }

        return ValidationResponse.builder()
                .exists(true)
                .active(userProfile.getActive())
                .kycVerified("VERIFIED".equals(userProfile.getKycStatus()))
                .message("UPI ID is valid")
                .build();

    }
}
