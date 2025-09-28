package com.ylcnfrht.blockchain.infrastructure.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ylcnfrht.blockchain.application.wallet.WalletApplicationService;
import com.ylcnfrht.blockchain.domain.wallet.Wallet;
import com.ylcnfrht.blockchain.infrastructure.web.dto.request.CreateWalletRequest;
import com.ylcnfrht.blockchain.infrastructure.web.dto.response.WalletBalanceResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

  private final WalletApplicationService walletService;

  @GetMapping
  @Operation(summary = "Get all wallets", description = "Retrieve all active wallets")

  public ResponseEntity<List<Wallet>> getAllWallets() {
    log.info("Getting all wallets");
    return ResponseEntity.ok(walletService.getAllWallets());
  }

  @GetMapping("/{id}")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Wallets found and returned"),
      @ApiResponse(responseCode = "400", description = "Invalid wallet ID format")
  })
  public ResponseEntity<Wallet> getWalletById(@PathVariable Long id) {
    log.info("Getting wallet by id: {}", id);
    return walletService.getWalletById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/address/{address}")

  public ResponseEntity<Wallet> getWalletByAddress(@PathVariable String address) {
    log.info("Getting wallet by address: {}", address);
    return walletService.getWalletByAddress(address)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/address/{address}/balance")
  public ResponseEntity<WalletBalanceResponse> getWalletBalance(@PathVariable String address) {
    log.info("Getting balance for address: {}", address);
    return ResponseEntity.ok(walletService.getWalletBalance(address));
  }

  @PostMapping
  public ResponseEntity<Wallet> createWallet(@Valid @RequestBody CreateWalletRequest request) {
    log.info("Creating wallet with address: {}", request.getAddress());
    Wallet wallet = walletService.createWallet(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Wallet> updateWallet(@PathVariable Long id,
      @Valid @RequestBody CreateWalletRequest request) {
    log.info("Updating wallet with id: {}", id);
    return walletService.updateWallet(id, request)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWallet(@PathVariable Long id) {
    log.info("Deleting wallet with id: {}", id);
    if (walletService.deleteWallet(id)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }

  @PatchMapping("/{id}/deactivate")
  public ResponseEntity<Wallet> deactivateWallet(@PathVariable Long id) {
    log.info("Deactivating wallet with id: {}", id);
    return walletService.deactivateWallet(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}