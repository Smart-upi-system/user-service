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
        log.info("gRPC: ValidateUserRequest called for upiId: {}", request.getUserId());


    }
}
