package com.uws.user_service.grpc;

import com.uws.user.grpc.proto.*;
import com.uws.user_service.model.UserProfile;
import com.uws.user_service.service.UserProfileService;
import com.uws.user_service.dto.UserProfileResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.util.ObjectUtils;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {

    private final UserProfileService userProfileService;

    @Override
    public void getUserByUpiId(GetUserByUpiIdRequest request, StreamObserver<UserResponse> streamObserver){
        log.info("gRPC: GetUserByUpiId called for upiId: {}", request.getUpiId());

        try{
            UserProfileResponse userProfile = userProfileService.getUserByUpiId(request.getUpiId());

            if(ObjectUtils.isEmpty(userProfile)){
                throw  new RuntimeException("user not found");
            }

            UserResponse userResponse= UserResponse.newBuilder()
                    .setUserId(userProfile.getUserId())
                    .setUpiId(userProfile.getUpiID())
                    .setFirstName(userProfile.getFirstName())
                    .setLastName(userProfile.getLastName() !=null ? userProfile.getLastName() : "")
                    .setActive(userProfile.getActive())
                    .setSuccess(true)
                    .setKycVerified("VERIFIED".equals(userProfile.getKycStatus()))
                    .setWalletId(userProfile.getWalletId() !=null ? userProfile.getWalletId() : "")
                    .setMessage("User found")
                    .build();

            streamObserver.onNext(userResponse);
            streamObserver.onCompleted();
            log.info("gRPC: User found - userId: {}, upiId: {}",
                    userProfile.getUserId(), userProfile.getUpiID());

        } catch (Exception e) {
            log.error("gRPC: Error retrieving user by UPI ID: {}", request.getUpiId(), e);
            UserResponse errorResponse = UserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("User not found")
                    .build();

            streamObserver.onNext(errorResponse);
            streamObserver.onCompleted();
        }
    }


    @Override
    public void validateUser(ValidateUserRequest request, StreamObserver<ValidationResponse> streamObserver){
        log.info("gRPC: ValidateUserRequest called for userId: {}", request.getUserId());

        try {
            com.uws.user_service.dto.ValidationResponse validation = userProfileService.validateUser(request.getUserId());
            ValidationResponse response= ValidationResponse.newBuilder()
                    .setValid(validation.isExists())
                    .setActive(validation.isActive())
                    .setKycVerified(validation.isKycVerified())
                    .setMessage(validation.getMessage())
                    .build();
            streamObserver.onNext(response);
            streamObserver.onCompleted();

        } catch (RuntimeException e) {
            log.error("gRPC: Error retrieving user by UPI ID: {}", request.getUserId(), e);
            ValidationResponse errorResponse = ValidationResponse.newBuilder()
                    .setValid(false)
                    .setActive(false)
                    .setKycVerified(false)
                    .setMessage("User not found or invalid")
                    .build();

            streamObserver.onNext(errorResponse);
            streamObserver.onCompleted();
        }
    }

    @Override
    public void getUserProfile(GetUserProfileRequest request,StreamObserver<com.uws.user.grpc.proto.UserProfileResponse> streamObserver){
        log.info("gRPC: GetUserProfileRequest called for userId: {}", request.getUserId());

        try {
            UserProfileResponse userProfile = userProfileService.getUserProfile(request.getUserId());
            if(ObjectUtils.isEmpty(userProfile)){
                throw  new RuntimeException("user not found");
            }
            com.uws.user.grpc.proto.UserProfileResponse userProfileResponse= com.uws.user.grpc.proto.UserProfileResponse.newBuilder()
                    .setUserId(userProfile.getUserId())
                    .setUpiId(userProfile.getUpiID())
                    .setActive(userProfile.getActive())
                    .setFirstName(userProfile.getFirstName())
                    .setLastName(userProfile.getLastName() !=null ? userProfile.getLastName() : "" )
                    .setAddress(userProfile.getAddress() !=null ? userProfile.getAddress() : "")
                    .setCity(userProfile.getCity() !=null ? userProfile.getCity() : "" )
                    .setPhone(userProfile.getPhone() !=null ? userProfile.getPhone() : "")
                    .setKycStatus(userProfile.getKycStatus())
                    .setState(userProfile.getState() !=null ? userProfile.getState() : "" )
                    .setPincode(userProfile.getPincode() !=null ? userProfile.getPincode() : "")
                    .setMessage("User profile retrieved success")
                    .setSuccess(true)
                    .build();

            streamObserver.onNext(userProfileResponse);
            streamObserver.onCompleted();
            log.info("gRPC: User profile fetched in here getUserProfile - userId: {}, active: {}, kycVerified: {}, address: {}",
                    userProfile.getUserId(), userProfile.getActive(),
                    "VERIFIED".equals(userProfile.getKycStatus()),userProfile.getAddress());

        } catch (Exception e) {
            log.error("gRPC: Error retrieving user profile: {}", request.getUserId(), e);

            com.uws.user.grpc.proto.UserProfileResponse errorResponse = com.uws.user.grpc.proto.UserProfileResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Profile not found")
                    .build();

            streamObserver.onNext(errorResponse);
            streamObserver.onCompleted();
        }

    }
}
