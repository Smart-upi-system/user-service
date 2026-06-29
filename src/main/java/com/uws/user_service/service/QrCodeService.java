package com.uws.user_service.service;

public interface QrCodeService {
    public byte[] getOrGenerateQrCode(String userId);
}
