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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;

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
      @RequestParam(required = false) OffsetDateTime from,
      @RequestParam(required = false) OffsetDateTime to) {
    Range<@NonNull Instant> timeRange = buildRange(from, to);
    return service.getTransactions(timeRange).stream()
        .map(TransactionController::toResponseDto)
        .toList();
  }

  @PostMapping("/transactions")
  @Operation(
      summary = "Create a new transaction",
      description =
          "Records a deposit or withdrawal. Supports only Euros as currency. Amount must be positive. Uses current UTC time if none is provided.")
  public TransactionResponseDto createTransaction(
      @Valid @RequestBody TransactionRequestDto request) {
    Transaction createdTransaction = service.addTransaction(toTransaction(request));
    return toResponseDto(createdTransaction);
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
        dto.occurredAt().map(OffsetDateTime::toInstant).orElse(Instant.now(clock)));
  }

  private static TransactionResponseDto toResponseDto(Transaction transaction) {
    return new TransactionResponseDto(
        transaction.getId(),
        transaction.getType(),
        transaction.getDescription(),
        transaction.getAmount(),
        transaction.getOccurredAt().atOffset(UTC));
  }

  private static Range<@NonNull Instant> buildRange(
      @Nullable OffsetDateTime from, @Nullable OffsetDateTime to) {
    @Nullable Instant fromUtc = from != null ? from.toInstant() : null;
    @Nullable Instant toUtc = to != null ? to.toInstant() : null;

    if (fromUtc != null && toUtc != null && !fromUtc.isBefore(toUtc)) {
      throw new BadRequestException("'from' must be before 'to'");
    }

    if (fromUtc != null && toUtc != null) return Range.closed(fromUtc, toUtc);
    if (fromUtc != null) return Range.atLeast(fromUtc);
    if (toUtc != null) return Range.atMost(toUtc);
    return Range.all();
  }
}
