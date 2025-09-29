package com.ylcnfrht.blockchain.infrastructure.persistence.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TransactionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @Column(name = "from_address")
  private String fromAddress;

  @Column(name = "to_address", nullable = false)
  private String toAddress;

  @Column(nullable = false, precision = 19, scale = 8)
  private BigDecimal amount;

  @Column(columnDefinition = "TEXT")
  private String signature;

  private LocalDateTime timestamp;

  @Column(name = "transaction_hash")
  private String transactionHash;

  @Column(nullable = false)
  @Builder.Default
  private boolean mined = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "block_id")
  @ToString.Exclude
  private BlockEntity block;
}
