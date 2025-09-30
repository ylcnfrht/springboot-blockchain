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

import com.ylcnfrht.blockchain.application.dtos.request.CreateWalletRequestDto;
import com.ylcnfrht.blockchain.application.dtos.response.CreateWalletResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.WalletBalanceResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.WalletResponseDto;
import com.ylcnfrht.blockchain.application.ports.WalletService;
import com.ylcnfrht.blockchain.infrastructure.web.result.ApiErrorCode;
import com.ylcnfrht.blockchain.infrastructure.web.result.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Wallet Management", description = "APIs for managing blockchain wallets")
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    @Operation(summary = "Get all wallets", description = "Retrieve all active wallets")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all wallets", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    public ResponseEntity<Result<List<WalletResponseDto>>> getAllWallets() {
        log.info("Getting all wallets");
        try {
            List<WalletResponseDto> wallets = walletService.getAllWallets();
            return ResponseEntity.ok(Result.success(wallets, "Wallets retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting all wallets", e);
            return ResponseEntity.status(500)
                .body(Result.error("Failed to retrieve wallets", ApiErrorCode.WALLET_ERROR));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get wallet by ID", description = "Retrieve a specific wallet by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet found and returned", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "Wallet not found", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    public ResponseEntity<Result<WalletResponseDto>> getWalletById(
            @Parameter(description = "Unique identifier of the wallet", example = "1", required = true) @PathVariable Long id) {
        log.info("Getting wallet by id: {}", id);
        try {
            return walletService.getWalletById(id)
                    .map(wallet -> ResponseEntity.ok(Result.success(wallet, "Wallet retrieved successfully")))
                    .orElse(ResponseEntity.status(404).body(Result.error("Wallet not found", ApiErrorCode.WALLET_NOT_FOUND)));
        } catch (Exception e) {
            log.error("Error getting wallet by id: {}", id, e);
            return ResponseEntity.status(500)
                .body(Result.error("Failed to retrieve wallet", ApiErrorCode.WALLET_ERROR));
        }
    }

    @GetMapping("/address/{address}")
    @Operation(summary = "Get wallet by address", description = "Retrieve a specific wallet by its blockchain address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet found and returned", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "Wallet not found", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    public ResponseEntity<Result<WalletResponseDto>> getWalletByAddress(
            @Parameter(description = "Blockchain address of the wallet", example = "04a1b2c3d4e5f6789...", required = true) @PathVariable String address) {
        log.info("Getting wallet by address: {}", address);
        try {
            return walletService.getWalletByAddress(address)
                    .map(wallet -> ResponseEntity.ok(Result.success(wallet, "Wallet retrieved successfully")))
                    .orElse(ResponseEntity.status(404).body(Result.error("Wallet not found", ApiErrorCode.WALLET_NOT_FOUND)));
        } catch (Exception e) {
            log.error("Error getting wallet by address: {}", address, e);
            return ResponseEntity.status(500)
                .body(Result.error("Failed to retrieve wallet", ApiErrorCode.WALLET_ERROR));
        }
    }

    @GetMapping("/address/{address}/balance")
    @Operation(summary = "Get wallet balance", description = "Retrieve the balance information for a specific wallet address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    public ResponseEntity<Result<WalletBalanceResponseDto>> getWalletBalance(
            @Parameter(description = "Blockchain address of the wallet", example = "04a1b2c3d4e5f6789...", required = true) @PathVariable String address) {
        log.info("Getting balance for address: {}", address);
        try {
            WalletBalanceResponseDto balance = walletService.getWalletBalance(address);
            return ResponseEntity.ok(Result.success(balance, "Balance retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting balance for address: {}", address, e);
            return ResponseEntity.status(500)
                .body(Result.error("Failed to retrieve balance", ApiErrorCode.BALANCE_ERROR));
        }
    }

    @PostMapping
    @Operation(summary = "Create new wallet", description = "Create a new blockchain wallet with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Wallet created successfully", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    public ResponseEntity<Result<CreateWalletResponseDto>> createWallet(
            @Parameter(description = "Wallet creation details", required = true) @Valid @RequestBody CreateWalletRequestDto request) {
        log.info("Creating wallet with address: {}", request.getAddress());
        try {
            CreateWalletResponseDto wallet = walletService.createWallet(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success(wallet, "Wallet created successfully"));
        } catch (Exception e) {
            log.error("Error creating wallet with address: {}", request.getAddress(), e);
            return ResponseEntity.status(500)
                .body(Result.error("Failed to create wallet", ApiErrorCode.WALLET_CREATION_ERROR));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update wallet", description = "Update an existing wallet with new details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet updated successfully", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "Wallet not found", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    public ResponseEntity<Result<WalletResponseDto>> updateWallet(
            @Parameter(description = "Unique identifier of the wallet to update", example = "1", required = true) @PathVariable Long id,
            @Parameter(description = "Updated wallet details", required = true) @Valid @RequestBody CreateWalletRequestDto request) {
        log.info("Updating wallet with id: {}", id);
        try {
            return walletService.updateWallet(id, request)
                    .map(wallet -> ResponseEntity.ok(Result.success(wallet, "Wallet updated successfully")))
                    .orElse(ResponseEntity.status(404).body(Result.error("Wallet not found", ApiErrorCode.WALLET_NOT_FOUND)));
        } catch (Exception e) {
            log.error("Error updating wallet with id: {}", id, e);
            return ResponseEntity.status(500)
                .body(Result.error("Failed to update wallet", ApiErrorCode.WALLET_UPDATE_ERROR));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete wallet", description = "Permanently delete a wallet from the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet deleted successfully", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "Wallet not found", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    public ResponseEntity<Result<Void>> deleteWallet(
            @Parameter(description = "Unique identifier of the wallet to delete", example = "1", required = true) @PathVariable Long id) {
        log.info("Deleting wallet with id: {}", id);
        try {
            if (walletService.deleteWallet(id)) {
                return ResponseEntity.ok(Result.success(null, "Wallet deleted successfully"));
            }
            return ResponseEntity.status(404).body(Result.error("Wallet not found", ApiErrorCode.WALLET_NOT_FOUND));
        } catch (Exception e) {
            log.error("Error deleting wallet with id: {}", id, e);
            return ResponseEntity.status(500)
                .body(Result.error("Failed to delete wallet", ApiErrorCode.WALLET_DELETION_ERROR));
        }
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate wallet", description = "Deactivate a wallet (soft delete) without permanently removing it")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet deactivated successfully", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "Wallet not found", content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
    })
    public ResponseEntity<Result<WalletResponseDto>> deactivateWallet(
            @Parameter(description = "Unique identifier of the wallet to deactivate", example = "1", required = true) @PathVariable Long id) {
        log.info("Deactivating wallet with id: {}", id);
        try {
            return walletService.deactivateWallet(id)
                    .map(wallet -> ResponseEntity.ok(Result.success(wallet, "Wallet deactivated successfully")))
                    .orElse(ResponseEntity.status(404).body(Result.error("Wallet not found", ApiErrorCode.WALLET_NOT_FOUND)));
        } catch (Exception e) {
            log.error("Error deactivating wallet with id: {}", id, e);
            return ResponseEntity.status(500)
                .body(Result.error("Failed to deactivate wallet", ApiErrorCode.WALLET_UPDATE_ERROR));
        }
    }
}