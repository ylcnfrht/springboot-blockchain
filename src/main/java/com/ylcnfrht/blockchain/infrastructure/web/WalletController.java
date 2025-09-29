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
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all wallets", content = @Content(schema = @Schema(implementation = WalletResponseDto.class)))
    })
    public ResponseEntity<List<WalletResponseDto>> getAllWallets() {
        log.info("Getting all wallets");
        return ResponseEntity.ok(walletService.getAllWallets());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get wallet by ID", description = "Retrieve a specific wallet by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet found and returned", content = @Content(schema = @Schema(implementation = WalletResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Wallet not found"),
            @ApiResponse(responseCode = "400", description = "Invalid wallet ID format")
    })
    public ResponseEntity<WalletResponseDto> getWalletById(
            @Parameter(description = "Unique identifier of the wallet", example = "1", required = true) @PathVariable Long id) {
        log.info("Getting wallet by id: {}", id);
        return walletService.getWalletById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/address/{address}")
    @Operation(summary = "Get wallet by address", description = "Retrieve a specific wallet by its blockchain address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet found and returned", content = @Content(schema = @Schema(implementation = WalletResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Wallet not found"),
            @ApiResponse(responseCode = "400", description = "Invalid address format")
    })
    public ResponseEntity<WalletResponseDto> getWalletByAddress(
            @Parameter(description = "Blockchain address of the wallet", example = "04a1b2c3d4e5f6789...", required = true) @PathVariable String address) {
        log.info("Getting wallet by address: {}", address);
        return walletService.getWalletByAddress(address)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/address/{address}/balance")
    @Operation(summary = "Get wallet balance", description = "Retrieve the balance information for a specific wallet address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully", content = @Content(schema = @Schema(implementation = WalletBalanceResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Wallet not found"),
            @ApiResponse(responseCode = "400", description = "Invalid address format")
    })
    public ResponseEntity<WalletBalanceResponseDto> getWalletBalance(
            @Parameter(description = "Blockchain address of the wallet", example = "04a1b2c3d4e5f6789...", required = true) @PathVariable String address) {
        log.info("Getting balance for address: {}", address);
        return ResponseEntity.ok(walletService.getWalletBalance(address));
    }

    @PostMapping
    @Operation(summary = "Create new wallet", description = "Create a new blockchain wallet with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Wallet created successfully", content = @Content(schema = @Schema(implementation = WalletResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data or validation errors"),
            @ApiResponse(responseCode = "409", description = "Wallet with this address already exists")
    })
    public ResponseEntity<CreateWalletResponseDto> createWallet(
            @Parameter(description = "Wallet creation details", required = true) @Valid @RequestBody CreateWalletRequestDto request) {
        log.info("Creating wallet with address: {}", request.getAddress());
        CreateWalletResponseDto wallet = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update wallet", description = "Update an existing wallet with new details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet updated successfully", content = @Content(schema = @Schema(implementation = WalletResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Wallet not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or validation errors")
    })
    public ResponseEntity<WalletResponseDto> updateWallet(
            @Parameter(description = "Unique identifier of the wallet to update", example = "1", required = true) @PathVariable Long id,
            @Parameter(description = "Updated wallet details", required = true) @Valid @RequestBody CreateWalletRequestDto request) {
        log.info("Updating wallet with id: {}", id);
        return walletService.updateWallet(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete wallet", description = "Permanently delete a wallet from the system")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Wallet deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Wallet not found"),
            @ApiResponse(responseCode = "400", description = "Invalid wallet ID format")
    })
    public ResponseEntity<Void> deleteWallet(
            @Parameter(description = "Unique identifier of the wallet to delete", example = "1", required = true) @PathVariable Long id) {
        log.info("Deleting wallet with id: {}", id);
        if (walletService.deleteWallet(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate wallet", description = "Deactivate a wallet (soft delete) without permanently removing it")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet deactivated successfully", content = @Content(schema = @Schema(implementation = WalletResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Wallet not found"),
            @ApiResponse(responseCode = "400", description = "Invalid wallet ID format")
    })
    public ResponseEntity<WalletResponseDto> deactivateWallet(
            @Parameter(description = "Unique identifier of the wallet to deactivate", example = "1", required = true) @PathVariable Long id) {
        log.info("Deactivating wallet with id: {}", id);
        return walletService.deactivateWallet(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}