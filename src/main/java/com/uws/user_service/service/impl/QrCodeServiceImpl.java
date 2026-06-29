package com.uws.user_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uws.user_service.exception.QRCodeException;
import com.uws.user_service.model.UserProfile;
import com.uws.user_service.repository.UserProfileRepository;
import com.uws.user_service.service.QrCodeService;
import com.uws.user_service.util.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class QrCodeServiceImpl implements QrCodeService {

    private final UserProfileRepository userProfileRepository;
    private final ObjectMapper objectMapper;

    /**
     * Returns the cached QR image bytes for the authenticated user.
     * On first call: generates the QR, saves the URL to UserProfile, caches the bytes.
     * On subsequent calls: returns from cache — no DB write, no re-generation.
     *
     * @param userId  the profile owner's userId (validated by the controller)
     * @return PNG byte array
     */

    @Cacheable(value = "qrCodeCache", key = "#userId")
    @Transactional
    public byte[] getOrGenerateQrCode(String userId){
        UserProfile userProfile=userProfileRepository.findByUserId(userId);
        if(userProfile==null){
            log.error("User profile not found for userId: {}", userId);
            throw new RuntimeException("User profile not found");
        }
        // If a QR was already generated before (e.g. server restart cleared cache),
        // we still re-generate the bytes since the payload hasn't changed.
        // The URL stored in the DB is just a reference/shareable link — not the image itself.
        byte[] qrByte=buildQrCodeByte(userProfile);

        if (userProfile.getQrCode() == null) {
            String qrUrl = "/users/qr-code" + userId; // Example URL; adjust as needed
            userProfile.setQrCode(qrUrl);
            userProfileRepository.save(userProfile);
            log.info("QR code URL persisted for userId={}", userId);
        }
        return qrByte;
    }

    private byte[] buildQrCodeByte(UserProfile userProfile) {
        try {
            String payload=buildPayload(userProfile);
            return QRCodeGenerator.generateQRCodeUtil(payload);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Builds the JSON payload embedded inside the QR code.
     * Static fields only — no timestamps or amounts, so the
     * QR image is identical on every generation for the same user.
     *
     * Output: {"userId":"...","upiId":"...","name":"John Doe"}
     */
    private String buildPayload(UserProfile userProfile) {
        try{
            Map<String,String> payloadMap=Map.of(
                    "userId",userProfile.getUserId(),
                    "upiId",userProfile.getUpiID(),
                    "number",userProfile.getPhone(),
                    "walletId",userProfile.getWalletId(),
                    "name",userProfile.getFirstName() + (userProfile.getLastName() != null ? " " + userProfile.getLastName() : "")
            );
            return  objectMapper.writeValueAsString(payloadMap);


        } catch (Exception e) {
            throw new QRCodeException("Failed to serialize QR payload", e);
        }
    }

}
