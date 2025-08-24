package io.github.francisco_f_silva.ledger_api.service;

import com.google.common.collect.Range;
import io.github.francisco_f_silva.ledger_api.model.Transaction;
import io.github.francisco_f_silva.ledger_api.model.TransactionType;
import io.github.francisco_f_silva.ledger_api.repo.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.github.francisco_f_silva.ledger_api.model.TransactionType.DEPOSIT;
import static io.github.francisco_f_silva.ledger_api.model.TransactionType.WITHDRAWAL;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests the service layer. For simplicity and more thorough testing, it does NOT mock the
 * repository, it uses the existing in-memory one. For a set-up with a database, this would need to
 * be changed.
 */
public class TransactionServiceTest {
  private static final Instant NOW = Instant.parse("2025-08-22T10:00:00Z");
  private static final Clock CLOCK = Clock.fixed(NOW, UTC);
  private static final Transaction T1_DEPOSIT =
      transaction(DEPOSIT, BigDecimal.valueOf(35.5), NOW.minus(2, DAYS));
  private static final Transaction T2_WITHDRAWAL =
      transaction(WITHDRAWAL, BigDecimal.valueOf(15.5), NOW.minus(1, DAYS));
  private static final Transaction T3_DEPOSIT = transaction(DEPOSIT, BigDecimal.valueOf(2.35), NOW);

  /** Actual (NOT mocked) in-memory repository. */
  private TransactionRepository repository;

  /** Service to be tested. */
  private TransactionService service;

  @BeforeEach
  void setUp() {
    repository = new TransactionRepository();
    service = new TransactionService(CLOCK, repository);
  }

  @DisplayName("Should return empty list when no transaction exists")
  @Test
  void getTransactions1() {
    List<Transaction> result = service.getTransactions(Range.all());
    assertThat(result).isEmpty();
  }

  @DisplayName(
      "Should return all transactions when given time range is endless and sort them from newest to oldest")
  @Test
  void getTransactions2() {
    storeTransactions(T1_DEPOSIT, T2_WITHDRAWAL, T3_DEPOSIT);
    List<Transaction> result = service.getTransactions(Range.all());
    assertThat(result).containsExactly(T3_DEPOSIT, T2_WITHDRAWAL, T1_DEPOSIT);
  }

  @DisplayName("Should return filtered transactions when given time range has lower endpoint")
  @Test
  void getTransactions3() {
    storeTransactions(T1_DEPOSIT, T2_WITHDRAWAL, T3_DEPOSIT);
    List<Transaction> result = service.getTransactions(Range.atLeast(NOW.minus(30, HOURS)));
    assertThat(result).containsExactly(T3_DEPOSIT, T2_WITHDRAWAL);
  }

  @DisplayName("Should return filtered transactions when given time range has upper endpoint")
  @Test
  void getTransactions4() {
    storeTransactions(T1_DEPOSIT, T2_WITHDRAWAL, T3_DEPOSIT);
    List<Transaction> result = service.getTransactions(Range.atMost(NOW.minus(30, HOURS)));
    assertThat(result).containsExactly(T1_DEPOSIT);
  }

  @DisplayName(
      "Should return filtered transactions when given time range has lower and upper endpoints")
  @Test
  void getTransactions5() {
    storeTransactions(T1_DEPOSIT, T2_WITHDRAWAL, T3_DEPOSIT);
    List<Transaction> result =
        service.getTransactions(Range.closed(NOW.minus(30, HOURS), NOW.minus(20, HOURS)));
    assertThat(result).containsExactly(T2_WITHDRAWAL);
  }

  @DisplayName("Should add transactions")
  @Test
  void addTransactions1() {
    service.addTransaction(T1_DEPOSIT);
    service.addTransaction(T2_WITHDRAWAL);
    assertThat(repository.getAllTransactions())
        .containsExactlyInAnyOrder(T1_DEPOSIT, T2_WITHDRAWAL);
  }

  @DisplayName("Fails when adding transactions in the future")
  @Test
  void addTransactions2() {
    Transaction futureTransaction = transaction(DEPOSIT, BigDecimal.ONE, NOW.plus(1, DAYS));
    assertThatThrownBy(() -> service.addTransaction(futureTransaction))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("Should support adding transactions older than other existing transactions")
  @Test
  void addTransactions3() {
    service.addTransaction(T1_DEPOSIT);
    service.addTransaction(T3_DEPOSIT);
    service.addTransaction(T2_WITHDRAWAL);
    assertThat(repository.getAllTransactions())
        .containsExactlyInAnyOrder(T1_DEPOSIT, T2_WITHDRAWAL, T3_DEPOSIT);
  }

  @DisplayName("Should return current balance as zero when no transactions exist")
  @Test
  void calculateCurrentBalance1() {
    BigDecimal result = service.calculateCurrentBalance();
    assertThat(result).isEqualTo(BigDecimal.ZERO);
  }

  @DisplayName("Should sum deposits and subtract withdrawals")
  @Test
  void calculateCurrentBalance2() {
    storeTransactions(T1_DEPOSIT, T2_WITHDRAWAL, T3_DEPOSIT);
    BigDecimal result = service.calculateCurrentBalance();
    assertThat(result).isEqualTo(BigDecimal.valueOf(22.35));
  }

  @DisplayName("Should support negative balances")
  @Test
  void calculateCurrentBalance3() {
    storeTransactions(
        T1_DEPOSIT, T2_WITHDRAWAL, transaction(WITHDRAWAL, BigDecimal.valueOf(30), NOW));
    BigDecimal result = service.calculateCurrentBalance();
    assertThat(result).isEqualTo(BigDecimal.valueOf(-10.0));
  }

  private void storeTransactions(Transaction... transactions) {
    Arrays.stream(transactions).forEach(repository::addTransaction);
  }

  private static Transaction transaction(
      TransactionType type, BigDecimal amount, Instant occurredAt) {
    return new Transaction(UUID.randomUUID(), type, "description", amount, occurredAt);
  }
}
