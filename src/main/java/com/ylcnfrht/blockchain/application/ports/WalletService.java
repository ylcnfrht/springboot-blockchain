package com.ylcnfrht.blockchain.application.ports;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.ylcnfrht.blockchain.application.dtos.request.CreateWalletRequestDto;
import com.ylcnfrht.blockchain.application.dtos.response.CreateWalletResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.KeyPairResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.WalletBalanceResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.WalletResponseDto;
import com.ylcnfrht.blockchain.domain.transaction.Transaction;

public interface WalletService {

  public List<WalletResponseDto> getAllWallets();
  public Optional<WalletResponseDto> getWalletById(Long id);
  public Optional<WalletResponseDto> getWalletByAddress(String address);
  public CreateWalletResponseDto createWallet(CreateWalletRequestDto request);
  public Optional<WalletResponseDto> updateWallet(Long id, CreateWalletRequestDto request);
  public boolean deleteWallet(Long id);
  public Optional<WalletResponseDto> deactivateWallet(Long id);
  public WalletBalanceResponseDto getWalletBalance(String address);
  public boolean hasEnoughBalance(String address, BigDecimal amount);
  public List<Transaction> getWalletTransactionHistory(String address);
  public void updateWalletBalancesAfterMining();
  public KeyPairResponseDto generateKeyPair();

}