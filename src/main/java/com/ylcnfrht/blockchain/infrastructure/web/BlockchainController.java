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

import com.ylcnfrht.blockchain.application.blockchain.BlockchainApplicationService;
import com.ylcnfrht.blockchain.domain.blockchain.Block;
import com.ylcnfrht.blockchain.infrastructure.web.dto.request.MineBlockRequest;
import com.ylcnfrht.blockchain.infrastructure.web.dto.response.BlockchainStatsResponse;

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

  @GetMapping("/hello")
  public String hello() {
    return "Blockchain API is running!";
  }

  @GetMapping("/blocks")
  @Operation(summary = "Get all blocks", description = "Retrieve all blocks in the blockchain ordered by creation time")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successfully retrieved all blocks"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<List<Block>> getAllBlocks() {
    log.info("Getting all blocks");
    return ResponseEntity.ok(blockchainService.getAllBlocks());
  }

  @GetMapping("/blocks/{id}")
  @Operation(summary = "Get block by ID", description = "Retrieve a specific block by its database ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Block found and returned"),
      @ApiResponse(responseCode = "404", description = "Block not found"),
      @ApiResponse(responseCode = "400", description = "Invalid block ID format")
  })
  public ResponseEntity<Block> getBlockById(
      @Parameter(description = "Database ID of the block", example = "1") @PathVariable Long id) {
    log.info("Getting block by id: {}", id);
    return blockchainService.getBlockById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/blocks/hash/{hash}")
  @Operation(summary = "Get block by hash", description = "Retrieve a specific block by its cryptographic hash")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Block found and returned"),
      @ApiResponse(responseCode = "404", description = "Block not found"),
      @ApiResponse(responseCode = "400", description = "Invalid hash format")
  })
  public ResponseEntity<Block> getBlockByHash(
      @Parameter(description = "Cryptographic hash of the block", example = "000abc123...") @PathVariable String hash) {
    log.info("Getting block by hash: {}", hash);
    return blockchainService.getBlockByHash(hash)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/blocks/latest")
  @Operation(summary = "Get latest block", description = "Retrieve the most recent block in the blockchain")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Latest block returned"),
      @ApiResponse(responseCode = "404", description = "No blocks found in the blockchain")
  })
  public ResponseEntity<Block> getLatestBlock() {
    log.info("Getting latest block");
    return blockchainService.getLatestBlock()
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/mine")
  @Operation(summary = "Mine a new block", description = "Mine all pending transactions into a new block and reward the miner")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Block successfully mined", content = @Content(schema = @Schema(implementation = Block.class))),
      @ApiResponse(responseCode = "400", description = "Invalid miner address"),
      @ApiResponse(responseCode = "500", description = "Mining operation failed")
  })
  public ResponseEntity<Block> mineBlock(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Mining request with miner address", required = true) @Valid @RequestBody MineBlockRequest request) {
    log.info("Mining block for miner: {}", request.getMinerAddress());
    Block minedBlock = blockchainService.minePendingTransactions(request.getMinerAddress());
    return ResponseEntity.status(HttpStatus.CREATED).body(minedBlock);
  }

  @GetMapping("/validate")
  @Operation(summary = "Validate blockchain", description = "Check the integrity of the entire blockchain by validating all blocks and their connections")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Validation completed"),
      @ApiResponse(responseCode = "500", description = "Validation failed due to server error")
  })
  public ResponseEntity<Boolean> validateChain() {
    log.info("Validating blockchain");
    return ResponseEntity.ok(blockchainService.isChainValid());
  }

  @GetMapping("/stats")
  @Operation(summary = "Get blockchain statistics", description = "Retrieve comprehensive statistics about the blockchain including blocks, transactions, and configuration")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully", content = @Content(schema = @Schema(implementation = BlockchainStatsResponse.class))),
      @ApiResponse(responseCode = "500", description = "Failed to retrieve statistics")
  })
  public ResponseEntity<BlockchainStatsResponse> getBlockchainStats() {
    log.info("Getting blockchain statistics");
    return ResponseEntity.ok(blockchainService.getBlockchainStats());
  }
}