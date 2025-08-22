package io.github.francisco_f_silva.ledger_api.service;

import com.google.common.collect.Range;
import io.github.francisco_f_silva.ledger_api.model.Transaction;
import io.github.francisco_f_silva.ledger_api.repo.TransactionRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class TransactionService {
  private final TransactionRepository repository;

  public TransactionService(TransactionRepository repository) {
    this.repository = repository;
  }

  /**
   * Returns a list of transactions occurring within the given time range, sorted from newest to
   * oldest.
   */
  public List<Transaction> getTransactions(Range<@NonNull Instant> timeRange) {
    return repository.getAllTransactions().stream()
        .filter(t -> timeRange.contains(t.getOccurredAt()))
        .sorted(Comparator.comparing(Transaction::getOccurredAt).reversed())
        .toList();
  }

  /** Stores the given transaction. */
  public Transaction addTransaction(Transaction transaction) {
    return repository.addTransaction(transaction);
  }

  /** Calculates current balance based on transaction history. */
  public BigDecimal calculateCurrentBalance() {
    return repository.getAllTransactions().stream()
        .map(this::extractSignedAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal extractSignedAmount(Transaction transaction) {
    return switch (transaction.getType()) {
      case DEPOSIT -> transaction.getAmount();
      case WITHDRAWAL -> transaction.getAmount().negate();
    };
  }
}
