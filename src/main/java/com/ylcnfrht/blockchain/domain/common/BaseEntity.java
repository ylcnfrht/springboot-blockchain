package com.ylcnfrht.blockchain.domain.common;

import java.time.LocalDateTime;
import java.util.Objects;

public class BaseEntity<T> {
  private T id;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private Boolean isActive;

  protected BaseEntity() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.deletedAt = null;
    this.isActive = true;
  }

  protected BaseEntity(T id) {
    this();
    this.id = id;
  }

  public T getId() {
    return id;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setId(T id) {
    this.id = id;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void delete() {
    this.deletedAt = LocalDateTime.now();
    this.isActive = false;
  }

  public void markAsModified() {
    this.updatedAt = LocalDateTime.now();
  }

  public void activate() {
    this.deletedAt = null;
    this.isActive = true;
  }

  public void deactivate() {
    this.deletedAt = LocalDateTime.now();
    this.isActive = false;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    BaseEntity<?> other = (BaseEntity<?>) obj;
    return Objects.equals(id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "BaseEntity [id=" + id + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", deletedAt=" + deletedAt
        + ", isActive=" + isActive + "]";
  }
}
