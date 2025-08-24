package io.github.francisco_f_silva.ledger_api.web;

import com.google.common.collect.Range;
import io.github.francisco_f_silva.ledger_api.model.Transaction;
import io.github.francisco_f_silva.ledger_api.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Transactions", description = "Handles deposits, withdrawals, and related balance.")
public class TransactionController {
  private final Clock clock;
  private final TransactionService service;

  public TransactionController(Clock clock, TransactionService service) {
    this.clock = clock;
    this.service = service;
  }

  @GetMapping("/transactions")
  @Operation(
      summary = "Fetch transactions",
      description =
          "Returns a list of transactions filtered according to parameters and sorted from newest to oldest.")
  public List<TransactionResponseDto> getTransactions(
      @RequestParam(required = false) Instant from, @RequestParam(required = false) Instant to) {
    Range<@NonNull Instant> timeRange = buildRange(from, to);
    return service.getTransactions(timeRange).stream()
        .map(TransactionController::toResponseDto)
        .toList();
  }

  @PostMapping("/transactions")
  @Operation(
      summary = "Create a new transaction",
      description =
          "Records a deposit or withdrawal. Supports only Euros as currency. Amount must be positive. "
              + "Accepts a UTC timestamp in the past, or uses current UTC time if none is provided.")
  public TransactionResponseDto createTransaction(
      @Valid @RequestBody TransactionRequestDto request) {
    try {
      Transaction createdTransaction = service.addTransaction(toTransaction(request));
      return toResponseDto(createdTransaction);
    } catch (IllegalArgumentException e) {
      throw new BadRequestException(e.getMessage(), e);
    }
  }

  @GetMapping("/balance")
  @Operation(
      summary = "View balance",
      description = "Returns current balance. Can be positive, zero, or negative.")
  public BalanceDto viewBalance() {
    BigDecimal balance = service.calculateCurrentBalance();
    return new BalanceDto(balance);
  }

  private Transaction toTransaction(TransactionRequestDto dto) {
    return new Transaction(
        UUID.randomUUID(),
        dto.type(),
        dto.description(),
        dto.amount().setScale(2, RoundingMode.HALF_UP),
        dto.occurredAt().orElse(Instant.now(clock)));
  }

  private static TransactionResponseDto toResponseDto(Transaction transaction) {
    return new TransactionResponseDto(
        transaction.getId(),
        transaction.getType(),
        transaction.getDescription(),
        transaction.getAmount(),
        transaction.getOccurredAt());
  }

  private static Range<@NonNull Instant> buildRange(@Nullable Instant from, @Nullable Instant to) {
    if (from != null && to != null && !from.isBefore(to)) {
      throw new BadRequestException("'from' must be before 'to'");
    }

    if (from != null && to != null) return Range.closed(from, to);
    if (from != null) return Range.atLeast(from);
    if (to != null) return Range.atMost(to);
    return Range.all();
  }
}
