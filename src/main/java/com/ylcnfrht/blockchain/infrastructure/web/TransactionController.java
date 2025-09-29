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
import com.ylcnfrht.blockchain.application.dtos.response.CreateTransactionResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.TransactionResponseDto;
import com.ylcnfrht.blockchain.application.ports.TransactionService;

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
      @ApiResponse(responseCode = "200", description = "Successfully retrieved all transactions"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<List<TransactionResponseDto>> getAllTransactions() {
    log.info("Getting all transactions");
    return ResponseEntity.ok(transactionService.getAllTransactions());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get transaction by ID", description = "Retrieve a specific transaction by its database ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transaction found and returned"),
      @ApiResponse(responseCode = "404", description = "Transaction not found"),
      @ApiResponse(responseCode = "400", description = "Invalid transaction ID format")
  })
  public ResponseEntity<TransactionResponseDto> getTransactionById(
      @Parameter(description = "Database ID of the transaction", example = "1") @PathVariable Long id) {
    log.info("Getting transaction by id: {}", id);
    return transactionService.getTransactionById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/hash/{hash}")
  @Operation(summary = "Get transaction by hash", description = "Retrieve a specific transaction by its cryptographic hash")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transaction found and returned"),
      @ApiResponse(responseCode = "404", description = "Transaction not found"),
      @ApiResponse(responseCode = "400", description = "Invalid hash format")
  })
  public ResponseEntity<TransactionResponseDto> getTransactionByHash(
      @Parameter(description = "Cryptographic hash of the transaction", example = "abc123def...") @PathVariable String hash) {
    log.info("Getting transaction by hash: {}", hash);
    return transactionService.getTransactionByHash(hash)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/address/{address}")
  @Operation(summary = "Get transactions by address", description = "Retrieve all transactions where the given address is either sender or receiver")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid address format")
  })
  public ResponseEntity<List<TransactionResponseDto>> getTransactionsByAddress(
      @Parameter(description = "Wallet address to search for", example = "wallet123abc...") @PathVariable String address) {
    log.info("Getting transactions for address: {}", address);
    return ResponseEntity.ok(transactionService.getTransactionsByAddress(address));
  }

  @GetMapping("/pending")
  @Operation(summary = "Get pending transactions", description = "Retrieve all transactions that are not yet included in a mined block")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Pending transactions retrieved successfully"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<List<TransactionResponseDto>> getPendingTransactions() {
    log.info("Getting pending transactions");
    return ResponseEntity.ok(transactionService.getPendingTransactions());
  }

  @PostMapping
  @Operation(summary = "Create a new transaction", description = "Create a new transaction and add it to the pending transactions pool")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Transaction created successfully", content = @Content(schema = @Schema(implementation = CreateTransactionResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Invalid transaction data or insufficient balance"),
      @ApiResponse(responseCode = "422", description = "Transaction validation failed")
  })
  public ResponseEntity<CreateTransactionResponseDto> createTransaction(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Transaction details", required = true) @Valid @RequestBody CreateTransactionRequestDto request) {
    log.info("Creating transaction from {} to {} amount {}",
        request.getFromAddress(), request.getToAddress(), request.getAmount());
    CreateTransactionResponseDto transaction = transactionService.createTransaction(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a transaction", description = "Update a pending transaction (only pending transactions can be updated)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
      @ApiResponse(responseCode = "404", description = "Transaction not found or already mined"),
      @ApiResponse(responseCode = "400", description = "Invalid transaction data"),
      @ApiResponse(responseCode = "409", description = "Transaction is already mined and cannot be updated")
  })
  public ResponseEntity<TransactionResponseDto> updateTransaction(
      @Parameter(description = "Database ID of the transaction to update", example = "1") @PathVariable Long id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated transaction details", required = true) @Valid @RequestBody CreateTransactionRequestDto request) {
    log.info("Updating transaction with id: {}", id);
    return transactionService.updateTransaction(id, request)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a transaction", description = "Delete a pending transaction (only pending transactions can be deleted)")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Transaction not found or already mined"),
      @ApiResponse(responseCode = "409", description = "Transaction is already mined and cannot be deleted")
  })
  public ResponseEntity<Void> deleteTransaction(
      @Parameter(description = "Database ID of the transaction to delete", example = "1") @PathVariable Long id) {
    log.info("Deleting transaction with id: {}", id);
    if (transactionService.deleteTransaction(id)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}