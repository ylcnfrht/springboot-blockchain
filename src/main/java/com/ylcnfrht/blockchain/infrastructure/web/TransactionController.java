package com.ylcnfrht.blockchain.infrastructure.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ylcnfrht.blockchain.application.dtos.request.CreateTransactionRequestDto;
import com.ylcnfrht.blockchain.application.dtos.request.SignTransactionRequestDto;
import com.ylcnfrht.blockchain.application.dtos.response.CreateTransactionResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.SignTransactionResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.TransactionResponseDto;
import com.ylcnfrht.blockchain.application.ports.TransactionService;
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
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transactions", description = "Transaction management operations")
public class TransactionController {

  private final TransactionService transactionService;

  @GetMapping
  @Operation(summary = "Get all transactions", description = "Retrieve all transactions including both pending and confirmed transactions")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successfully retrieved all transactions", content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<List<TransactionResponseDto>>> getAllTransactions() {
    log.info("Getting all transactions");
    try {
      List<TransactionResponseDto> transactions = transactionService.getAllTransactions();
      return ResponseEntity.ok(Result.success(transactions, "Transactions retrieved successfully"));
    } catch (Exception e) {
      log.error("Error getting all transactions", e);
      return ResponseEntity.status(500)
        .body(Result.error("Failed to retrieve transactions", ApiErrorCode.TRANSACTION_ERROR));
    }
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get transaction by ID", description = "Retrieve a specific transaction by its database ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transaction found and returned", content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<TransactionResponseDto>> getTransactionById(
      @Parameter(description = "Database ID of the transaction", example = "1") @PathVariable Long id) {
    log.info("Getting transaction by id: {}", id);
    try {
      return transactionService.getTransactionById(id)
          .map(transaction -> ResponseEntity.ok(Result.success(transaction, "Transaction retrieved successfully")))
          .orElse(ResponseEntity.status(404).body(Result.error("Transaction not found", ApiErrorCode.TRANSACTION_NOT_FOUND)));
    } catch (Exception e) {
      log.error("Error getting transaction by id: {}", id, e);
      return ResponseEntity.status(500)
        .body(Result.error("Failed to retrieve transaction", ApiErrorCode.TRANSACTION_ERROR));
    }
  }

  @GetMapping("/hash/{hash}")
  @Operation(summary = "Get transaction by hash", description = "Retrieve a specific transaction by its cryptographic hash")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transaction found and returned", content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<TransactionResponseDto>> getTransactionByHash(
      @Parameter(description = "Cryptographic hash of the transaction", example = "abc123def...") @PathVariable String hash) {
    log.info("Getting transaction by hash: {}", hash);
    try {
      return transactionService.getTransactionByHash(hash)
          .map(transaction -> ResponseEntity.ok(Result.success(transaction, "Transaction retrieved successfully")))
          .orElse(ResponseEntity.status(404).body(Result.error("Transaction not found", ApiErrorCode.TRANSACTION_NOT_FOUND)));
    } catch (Exception e) {
      log.error("Error getting transaction by hash: {}", hash, e);
      return ResponseEntity.status(500)
        .body(Result.error("Failed to retrieve transaction", ApiErrorCode.TRANSACTION_ERROR));
    }
  }

  @GetMapping("/address/{address}")
  @Operation(summary = "Get transactions by address", description = "Retrieve all transactions where the given address is either sender or receiver")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully", content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<List<TransactionResponseDto>>> getTransactionsByAddress(
      @Parameter(description = "Wallet address to search for", example = "wallet123abc...") @PathVariable String address) {
    log.info("Getting transactions for address: {}", address);
    try {
      List<TransactionResponseDto> transactions = transactionService.getTransactionsByAddress(address);
      return ResponseEntity.ok(Result.success(transactions, "Transactions retrieved successfully"));
    } catch (Exception e) {
      log.error("Error getting transactions for address: {}", address, e);
      return ResponseEntity.status(500)
        .body(Result.error("Failed to retrieve transactions", ApiErrorCode.TRANSACTION_ERROR));
    }
  }

  @GetMapping("/pending")
  @Operation(summary = "Get pending transactions", description = "Retrieve all transactions that are not yet included in a mined block")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Pending transactions retrieved successfully", content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<List<TransactionResponseDto>>> getPendingTransactions() {
    log.info("Getting pending transactions");
    try {
      List<TransactionResponseDto> transactions = transactionService.getPendingTransactions();
      return ResponseEntity.ok(Result.success(transactions, "Pending transactions retrieved successfully"));
    } catch (Exception e) {
      log.error("Error getting pending transactions", e);
      return ResponseEntity.status(500)
        .body(Result.error("Failed to retrieve pending transactions", ApiErrorCode.TRANSACTION_ERROR));
    }
  }

  @PostMapping
  @Operation(summary = "Create a new transaction", description = "Create a new transaction and add it to the pending transactions pool")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Transaction created successfully", content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<CreateTransactionResponseDto>> createTransaction(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Transaction details", required = true) @Valid @RequestBody CreateTransactionRequestDto request) {
    log.info("Creating transaction from {} to {} amount {}",
        request.getFromAddress(), request.getToAddress(), request.getAmount());
    try {
      CreateTransactionResponseDto transaction = transactionService.createTransaction(request);
      return ResponseEntity.status(HttpStatus.CREATED)
        .body(Result.success(transaction, "Transaction created successfully"));
    } catch (Exception e) {
      log.error("Error creating transaction from {} to {} amount {}",
          request.getFromAddress(), request.getToAddress(), request.getAmount(), e);
      return ResponseEntity.status(500)
        .body(Result.error("Failed to create transaction", ApiErrorCode.TRANSACTION_CREATION_ERROR));
    }
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a transaction", description = "Update a pending transaction (only pending transactions can be updated)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transaction updated successfully", content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<TransactionResponseDto>> updateTransaction(
      @Parameter(description = "Database ID of the transaction to update", example = "1") @PathVariable Long id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated transaction details", required = true) @Valid @RequestBody CreateTransactionRequestDto request) {
    log.info("Updating transaction with id: {}", id);
    try {
      return transactionService.updateTransaction(id, request)
          .map(transaction -> ResponseEntity.ok(Result.success(transaction, "Transaction updated successfully")))
          .orElse(ResponseEntity.status(404).body(Result.error("Transaction not found", ApiErrorCode.TRANSACTION_NOT_FOUND)));
    } catch (Exception e) {
      log.error("Error updating transaction with id: {}", id, e);
      return ResponseEntity.status(500)
        .body(Result.error("Failed to update transaction", ApiErrorCode.TRANSACTION_UPDATE_ERROR));
    }
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a transaction", description = "Delete a pending transaction (only pending transactions can be deleted)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transaction deleted successfully", content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<Void>> deleteTransaction(
      @Parameter(description = "Database ID of the transaction to delete", example = "1") @PathVariable Long id) {
    log.info("Deleting transaction with id: {}", id);
    try {
      if (transactionService.deleteTransaction(id)) {
        return ResponseEntity.ok(Result.success(null, "Transaction deleted successfully"));
      }
      return ResponseEntity.status(404).body(Result.error("Transaction not found", ApiErrorCode.TRANSACTION_NOT_FOUND));
    } catch (Exception e) {
      log.error("Error deleting transaction with id: {}", id, e);
      return ResponseEntity.status(500)
        .body(Result.error("Failed to delete transaction", ApiErrorCode.TRANSACTION_DELETION_ERROR));
    }
  }

  @PostMapping("/sign")
  @Operation(summary = "Sign a transaction", description = "Cryptographically sign a pending transaction using ECDSA")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transaction signed successfully", content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request or transaction cannot be signed", content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<SignTransactionResponseDto>> signTransaction(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Transaction signing details", required = true) @Valid @RequestBody SignTransactionRequestDto request) {
    log.info("Signing transaction with id: {}", request.getTransactionId());
    try {
      SignTransactionResponseDto result = transactionService.signTransaction(request);
      return ResponseEntity.ok(Result.success(result, result.getMessage()));
    } catch (Exception e) {
      log.error("Error signing transaction with id: {}", request.getTransactionId(), e);
      return ResponseEntity.status(500)
        .body(Result.error("Failed to sign transaction", ApiErrorCode.TRANSACTION_SIGNING_ERROR));
    }
  }
}