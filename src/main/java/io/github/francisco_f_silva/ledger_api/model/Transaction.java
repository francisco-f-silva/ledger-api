package io.github.francisco_f_silva.ledger_api.model;

import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/** Represents a movement of money in or out the account managed in the system. */
public class Transaction {
  /** Unique ID of the transaction. */
  private final UUID id;

  /** Type of the transaction (e.g. withdrawal, deposit). */
  private final TransactionType type;

  /** Textual description (e.g. Groceries). */
  private final String description;

  /** Amount in Euros. Always positive. */
  private final BigDecimal amount;

  /** Date and time in UTC at which the transaction occurred. */
  private final Instant occurredAt;

  public Transaction(
      UUID id, TransactionType type, String description, BigDecimal amount, Instant occurredAt) {
    checkNotNull(id, "transaction id can not be null");
    checkNotNull(type, "transaction type can not be null");
    checkArgument(
        !Strings.isNullOrEmpty(description), "transaction description can not be null or empty");
    checkNotNull(amount, "transaction amount can not be null");
    checkArgument(amount.compareTo(BigDecimal.ZERO) > 0, "transaction amount must be positive");
    checkNotNull(occurredAt, "transaction occurredAt can not be null");

    this.id = id;
    this.type = type;
    this.description = description;
    this.amount = amount;
    this.occurredAt = occurredAt;
  }

  public UUID getId() {
    return id;
  }

  public TransactionType getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Instant getOccurredAt() {
    return occurredAt;
  }

  /** Two transactions are equal when they have the same ID. */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Transaction that)) return false;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
