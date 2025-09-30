package com.ylcnfrht.blockchain.application.mappers;

import org.springframework.stereotype.Component;

import com.ylcnfrht.blockchain.application.dtos.response.CreateWalletResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.KeyPairResponseDto;
import com.ylcnfrht.blockchain.infrastructure.crypto.KeyPairGenerator.KeyPairStrings;
import com.ylcnfrht.blockchain.application.dtos.response.WalletBalanceResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.WalletResponseDto;
import com.ylcnfrht.blockchain.domain.wallet.Wallet;

@Component
public class WalletDtoMapper {

  public CreateWalletResponseDto toCreateWalletResponseDto(Wallet wallet) {
    return CreateWalletResponseDto.builder()
        .id(wallet.getId().getValue())
        .address(wallet.getAddress().getValue())
        .publicKey(wallet.getPublicKey())
        .balance(wallet.getBalance().getValue())
        .createdAt(wallet.getCreatedAt())
        .active(wallet.isActive())
        .build();
  }

  public WalletResponseDto toWalletResponseDto(Wallet wallet) {
    return WalletResponseDto.builder()
        .id(wallet.getId().getValue())
        .address(wallet.getAddress().getValue())
        .publicKey(wallet.getPublicKey())
        .balance(wallet.getBalance().getValue())
        .createdAt(wallet.getCreatedAt())
        .active(wallet.isActive())
        .build();
  }

  public WalletBalanceResponseDto toWalletBalanceResponseDto(
      String address, 
      java.math.BigDecimal balance, 
      java.math.BigDecimal pendingBalance) {
    return WalletBalanceResponseDto.builder()
        .address(address)
        .balance(balance)
        .pendingBalance(pendingBalance)
        .build();
  }

  public KeyPairResponseDto toKeyPairResponseDto(KeyPairStrings keyPair, String algorithm) {
    return KeyPairResponseDto.builder()
        .publicKey(keyPair.getPublicKey())
        .privateKey(keyPair.getPrivateKey())
        .algorithm(algorithm)
        .build();
  }
}
