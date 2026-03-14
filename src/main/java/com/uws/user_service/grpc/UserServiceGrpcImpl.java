package com.uws.user_service.grpc;

import com.uws.user.grpc.proto.*;
import com.uws.user_service.model.UserProfile;
import com.uws.user_service.repository.UserProfileRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.util.ObjectUtils;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {

    private final UserProfileRepository userProfileRepository;

    @Override
    public void getUserByUpiId(GetUserByUpiIdRequest request, StreamObserver<UserResponse> streamObserver){
        log.info("gRPC: GetUserByUpiId called for upiId: {}", request.getUpiId());

        try{
            UserProfile userProfile=userProfileRepository.findByUpiId(request.getUpiId());
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
        UserProfile userProfile=userProfileRepository.findByUserId(request.getUserId());
            ValidationResponse response= ValidationResponse.newBuilder()
                    .setValid(true)
                    .setActive(userProfile.getActive())
                    .setKycVerified("VERIFIED".equals(userProfile.getKycStatus()))
                    .setMessage("User is valid")
                    .build();
            streamObserver.onNext(response);
            streamObserver.onCompleted();

            log.info("gRPC: User validated - userId: {}, active: {}, kycVerified: {}",
                    userProfile.getUserId(), userProfile.getActive(),
                    "VERIFIED".equals(userProfile.getKycStatus()));

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
    public void getUserProfile(GetUserProfileRequest request,StreamObserver<UserProfileResponse> streamObserver){
        log.info("gRPC: GetUserProfileRequest called for userId: {}", request.getUserId());
        try {
            UserProfile userProfile=userProfileRepository.findByUserId(request.getUserId());
            if(ObjectUtils.isEmpty(userProfile)){
                throw  new RuntimeException("user not found");
            }
            UserProfileResponse userProfileResponse= UserProfileResponse.newBuilder()
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

            UserProfileResponse errorResponse = UserProfileResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Profile not found")
                    .build();

            streamObserver.onNext(errorResponse);
            streamObserver.onCompleted();
        }

    }
}
