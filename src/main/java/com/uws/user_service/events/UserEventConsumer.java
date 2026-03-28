package com.uws.user_service.events;

import com.uws.user_service.grpc.WalletServiceGrpcClient;
import com.uws.user_service.model.UserProfile;
import com.uws.user_service.repository.UserProfileRepository;
import com.uws.user_service.util.UpiIdGenerator;
import com.uws.wallet.grpc.proto.WalletResponse;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserEventConsumer {

    private final UserProfileRepository userProfileRepository;
    private final UpiIdGenerator upiIdGenerator;
    private final WalletServiceGrpcClient walletGrpcClient;

    @KafkaListener(
            topics = "${kafka.topics.user-events}",
            groupId = "${spring.kafka.consumer.group-id:user-profile}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handleUserEventCreated(UserCreatedEvent userCreatedEvent){
        log.info("Received UserCreatedEvent: eventId={}, userId={}",
                userCreatedEvent.getEventId(), userCreatedEvent.getUserId());

        try{
            if(userProfileRepository.existsById(userCreatedEvent.getUserId())){
                log.warn("UserProfile already exists for userId: {}. Skipping duplicate event.",
                        userCreatedEvent.getUserId());
                return;
            }

            String[] nameParts=parseFullName(userCreatedEvent.getData().getName());
            String firstName=nameParts[0];
            String lastName=nameParts[1];

            String upiId=upiIdGenerator.generateUpiId(userCreatedEvent.getData().getUsername());

            UserProfile profile=UserProfile.builder()
                    .userId(userCreatedEvent.getUserId())
                    .id(userCreatedEvent.getUserId())
                    .firstName(firstName)
                    .lastName(lastName)
                    .upiID(upiId)
                    .kycStatus("PENDING")
                    .active(true)
                    .build();

            userProfileRepository.save(profile);
            log.info("UserProfile created successfully: userId={}, upiId={}",
                    userCreatedEvent.getUserId(), upiId);

            // Call Wallet Service to create the wallet
            WalletResponse response = walletGrpcClient.createWallet(userCreatedEvent.getUserId());
            if (response.getSuccess()) {
                log.info("Wallet created for user {}. ID: {}", userCreatedEvent.getUserId(), response.getWalletId());
            } else {
                // Throwing exception forces Transactional rollback of UserProfile
                throw new RuntimeException("Wallet Service Error: " + response.getMessage());
            }

        } catch (Exception e) {
            log.error("Failed to process UserCreatedEvent: eventId={}, userId={}",
                    userCreatedEvent.getEventId(), userCreatedEvent.getUserId(), e);
            throw e;
        }


    }

    private String[] parseFullName(String name) {
        if(name==null || name.trim().isEmpty()){
            return new String[]{"Unknown",""};
        }
        String[] parts=name.trim().split("\\s+",2);
        String firstName=parts[0];
        String lastName=parts.length > 1 ? parts[1] : "";

        return new String[] {firstName,lastName};
    }

}
