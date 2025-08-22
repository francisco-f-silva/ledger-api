package io.github.francisco_f_silva.ledger_api.repo;

import io.github.francisco_f_silva.ledger_api.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

@Repository
public class TransactionRepository {
  private final Map<UUID, Transaction> transactions = new HashMap<>();

  /** Returns a list of transactions. */
  public List<Transaction> getAllTransactions() {
    return transactions.values().stream().toList();
  }

  /** Stores the given transaction, or throws {@link IllegalArgumentException} if already exists. */
  public Transaction addTransaction(Transaction transaction) {
    checkArgument(
        !transactions.containsKey(transaction.getId()),
        "transaction with id %s already exists".formatted(transaction.getId()));

    transactions.put(transaction.getId(), transaction);
    return transaction;
  }
}
