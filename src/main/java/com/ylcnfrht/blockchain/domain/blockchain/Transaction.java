package com.ylcnfrht.blockchain.domain.blockchain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Transaction {
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
  private String transactionHash;

  @Column(nullable = false)
  @Builder.Default
  private boolean mined = false;

  @ManyToOne
  @JoinColumn(name = "block_id")
  @JsonBackReference
  @ToString.Exclude
  private Block block;
}
