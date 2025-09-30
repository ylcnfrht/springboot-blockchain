package com.ylcnfrht.blockchain.infrastructure.persistence.mappers;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.ylcnfrht.blockchain.domain.blockchain.valueobjects.Balance;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.domain.wallet.Wallet;
import com.ylcnfrht.blockchain.domain.wallet.valueobjects.Address;
import com.ylcnfrht.blockchain.infrastructure.persistence.entities.WalletEntity;

@Component
public class WalletMapper {

  public Wallet toDomain(WalletEntity entity) {
    if (entity == null) return null;
    Wallet wallet = Wallet.of(
        Id.of(entity.getId()),
        Address.of(entity.getAddress()),
        entity.getPublicKey(),
        entity.getPrivateKey(),
        entity.getBalance() != null ? Balance.of(entity.getBalance()) : Balance.of(BigDecimal.ZERO),
        entity.getActive()
    );
    if (entity.getCreatedAt() != null) {
      wallet.setCreatedAt(entity.getCreatedAt());
    }
    return wallet;
  }

  public WalletEntity toEntity(Wallet wallet) {
    if (wallet == null) return null;
    WalletEntity entity = new WalletEntity();
    entity.setId(wallet.getId() != null ? wallet.getId().getValue() : null);
    entity.setAddress(wallet.getAddress() != null ? wallet.getAddress().getValue() : null);
    entity.setPublicKey(wallet.getPublicKey());
    entity.setPrivateKey(wallet.getPrivateKey());
    entity.setBalance(wallet.getBalance() != null ? wallet.getBalance().getValue() : BigDecimal.ZERO);
    entity.setActive(wallet.getIsActive());
    return entity;
  }
}


