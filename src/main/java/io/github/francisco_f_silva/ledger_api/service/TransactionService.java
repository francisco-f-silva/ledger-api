package io.github.francisco_f_silva.ledger_api.service;

import io.github.francisco_f_silva.ledger_api.model.Transaction;
import io.github.francisco_f_silva.ledger_api.repo.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
  private final TransactionRepository repository;

  public TransactionService(TransactionRepository repository) {
    this.repository = repository;
  }

  public Transaction addTransaction(Transaction transaction) {
    return repository.addTransaction(transaction);
  }
}
