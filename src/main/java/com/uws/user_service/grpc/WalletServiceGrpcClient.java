package com.uws.user_service.grpc;

import com.uws.wallet.grpc.proto.WalletServiceGrpc;
import com.uws.wallet.grpc.proto.CreateWalletRequest;
import com.uws.wallet.grpc.proto.WalletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Slf4j
@Service
public class WalletServiceGrpcClient {

    @GrpcClient("wallet-service")
    private WalletServiceGrpc.WalletServiceBlockingStub walletStub;

    public WalletResponse createWallet(String userId) {
        CreateWalletRequest request = CreateWalletRequest.newBuilder()
                .setUserId(userId)
                .build();
        return walletStub.createWallet(request);
    }
}
