package com.ylcnfrht.blockchain.infrastructure.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ylcnfrht.blockchain.application.dtos.request.MineBlockRequestDto;
import com.ylcnfrht.blockchain.application.dtos.response.BlockResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.BlockchainStatsResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.CreateBlockResponseDto;
import com.ylcnfrht.blockchain.application.ports.BlockchainApplicationService;
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
@RequestMapping("/api/blockchain")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Blockchain", description = "Blockchain management operations")
public class BlockchainController {

  private final BlockchainApplicationService blockchainService;

  @GetMapping("/blocks")
  @Operation(summary = "Get all blocks", description = "Retrieve all blocks in the blockchain ordered by creation time")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successfully retrieved all blocks", 
          content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<List<BlockResponseDto>>> getAllBlocks() {
    log.info("Getting all blocks");
    try {
      List<BlockResponseDto> blocks = blockchainService.getAllBlocks();
      return ResponseEntity.ok(Result.success(blocks, "Blocks retrieved successfully"));
    } catch (Exception e) {
      log.error("Error getting blocks", e);
      return ResponseEntity.status(500)
          .body(Result.error("Failed to retrieve blocks", ApiErrorCode.BLOCKCHAIN_ERROR));
    }
  }

  @GetMapping("/blocks/{id}")
  @Operation(summary = "Get block by ID", description = "Retrieve a specific block by its database ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Block found and returned",
          content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "404", description = "Block not found",
          content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<BlockResponseDto>> getBlockById(
      @Parameter(description = "Database ID of the block", example = "1") @PathVariable Long id) {
    log.info("Getting block by id: {}", id);
    try {
      return blockchainService.getBlockById(id)
          .map(block -> ResponseEntity.ok(Result.success(block, "Block retrieved successfully")))
          .orElse(ResponseEntity.status(404).body(Result.error("Block not found", ApiErrorCode.BLOCK_NOT_FOUND)));
    } catch (Exception e) {
      log.error("Error getting block by id: {}", id, e);
      return ResponseEntity.status(500)
          .body(Result.error("Failed to retrieve block", ApiErrorCode.BLOCKCHAIN_ERROR));
    }
  }

  @GetMapping("/blocks/hash/{hash}")
  @Operation(summary = "Get block by hash", description = "Retrieve a specific block by its cryptographic hash")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Block found and returned",
          content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "404", description = "Block not found",
          content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<BlockResponseDto>> getBlockByHash(
      @Parameter(description = "Cryptographic hash of the block", example = "000abc123...") @PathVariable String hash) {
    log.info("Getting block by hash: {}", hash);
    try {
      return blockchainService.getBlockByHash(hash)
          .map(block -> ResponseEntity.ok(Result.success(block, "Block retrieved successfully")))
          .orElse(ResponseEntity.status(404).body(Result.error("Block not found", ApiErrorCode.BLOCK_NOT_FOUND)));
    } catch (Exception e) {
      log.error("Error getting block by hash: {}", hash, e);
      return ResponseEntity.status(500)
          .body(Result.error("Failed to retrieve block", ApiErrorCode.BLOCKCHAIN_ERROR));
    }
  }

  @GetMapping("/blocks/latest")
  @Operation(summary = "Get latest block", description = "Retrieve the most recent block in the blockchain")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Latest block returned",
          content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "404", description = "No blocks found in the blockchain",
          content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<BlockResponseDto>> getLatestBlock() {
    log.info("Getting latest block");
    try {
      return blockchainService.getLatestBlock()
          .map(block -> ResponseEntity.ok(Result.success(block, "Latest block retrieved successfully")))
          .orElse(ResponseEntity.status(404).body(Result.error("No blocks found", ApiErrorCode.NO_BLOCKS_FOUND)));
    } catch (Exception e) {
      log.error("Error getting latest block", e);
      return ResponseEntity.status(500)
          .body(Result.error("Failed to retrieve latest block", ApiErrorCode.BLOCKCHAIN_ERROR));
    }
  }

  @PostMapping("/mine")
  @Operation(summary = "Mine a new block", description = "Mine all pending transactions into a new block and reward the miner")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Block successfully mined", 
          content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Mining operation failed",
          content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<CreateBlockResponseDto>> mineBlock(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Mining request with miner address", required = true) @Valid @RequestBody MineBlockRequestDto request) {
    log.info("Mining block for miner: {}", request.getMinerAddress());
    try {
      CreateBlockResponseDto minedBlock = blockchainService.minePendingTransactions(request.getMinerAddress());
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(Result.success(minedBlock, "Block mined successfully"));
    } catch (Exception e) {
      log.error("Error mining block for miner: {}", request.getMinerAddress(), e);
      return ResponseEntity.status(500)
          .body(Result.error("Failed to mine block", ApiErrorCode.MINING_ERROR));
    }
  }

  @GetMapping("/validate")
  @Operation(summary = "Validate blockchain", description = "Check the integrity of the entire blockchain by validating all blocks and their connections")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Validation completed",
          content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Validation failed due to server error",
          content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<Boolean>> validateChain() {
    log.info("Validating blockchain");
    try {
      boolean isValid = blockchainService.isChainValid();
      return ResponseEntity.ok(Result.success(isValid, 
          isValid ? "Blockchain is valid" : "Blockchain validation failed"));
    } catch (Exception e) {
      log.error("Error validating blockchain", e);
      return ResponseEntity.status(500)
          .body(Result.error("Failed to validate blockchain", ApiErrorCode.VALIDATION_ERROR));
    }
  }

  @GetMapping("/stats")
  @Operation(summary = "Get blockchain statistics", description = "Retrieve comprehensive statistics about the blockchain including blocks, transactions, and configuration")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully", 
          content = @Content(schema = @Schema(implementation = Result.class))),
      @ApiResponse(responseCode = "500", description = "Failed to retrieve statistics",
          content = @Content(schema = @Schema(implementation = Result.class)))
  })
  public ResponseEntity<Result<BlockchainStatsResponseDto>> getBlockchainStats() {
    log.info("Getting blockchain statistics");
    try {
      BlockchainStatsResponseDto stats = blockchainService.getBlockchainStats();
      return ResponseEntity.ok(Result.success(stats, "Blockchain statistics retrieved successfully"));
    } catch (Exception e) {
      log.error("Error getting blockchain statistics", e);
      return ResponseEntity.status(500)
          .body(Result.error("Failed to retrieve blockchain statistics", ApiErrorCode.STATS_ERROR));
    }
  }
}