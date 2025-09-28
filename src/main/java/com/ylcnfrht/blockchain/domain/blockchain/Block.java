package com.ylcnfrht.blockchain.domain.blockchain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "blocks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Block {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @Column(unique = true, nullable = false)
  @EqualsAndHashCode.Include
  private String hash;

  @Column(name = "previous_hash")
  private String previousHash;

  @Column(nullable = false)
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();

  @Column(nullable = false)
  @Builder.Default
  private Integer nonce = 0;

  @Column(nullable = false)
  @Builder.Default
  private Boolean mined = false;

  @OneToMany(mappedBy = "block", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  @Builder.Default
  @ToString.Exclude
  private List<Transaction> transactions = new ArrayList<>();

  public void addTransaction(Transaction transaction) {
    transactions.add(transaction);
    transaction.setBlock(this);
  }
}
