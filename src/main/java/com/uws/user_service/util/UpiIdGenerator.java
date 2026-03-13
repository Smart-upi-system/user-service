package com.uws.user_service.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpiIdGenerator {

    private static final String UPI_SUFFIX="@wallet";


    public String generateUpiId(String username) {
        if(username==null || username.trim().isEmpty()){
            log.error("Username is empty please retry");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        String sanitizedUserName=username.trim().toLowerCase();
        String upiId=sanitizedUserName + UPI_SUFFIX;

        log.debug("Generated UPI ID: {} from username: {}", upiId, username);
        return  upiId;
    }

    public boolean isValidUpiId(String upiId){
        if(upiId==null || upiId.trim().isEmpty()) {
            return false;
        }

        return upiId.matches("^[a-z0-9._-]+@wallet$");
    }

    public String extractUserName(String upiId){
        if(!isValidUpiId(upiId)) {
            throw new IllegalArgumentException("Invalid UPI ID format: " + upiId);
        }
        return upiId.substring(0,upiId.lastIndexOf("@"));
    }
}
