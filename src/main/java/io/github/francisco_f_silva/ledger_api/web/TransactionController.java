package io.github.francisco_f_silva.ledger_api.web;

import io.github.francisco_f_silva.ledger_api.model.Transaction;
import io.github.francisco_f_silva.ledger_api.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;

@RestController
@Tag(name = "Transactions", description = "Handles deposits, withdrawals, and related balance.")
public class TransactionController {
  private final Clock clock;
  private final TransactionService service;

  public TransactionController(Clock clock, TransactionService service) {
    this.clock = clock;
    this.service = service;
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

  private Transaction toTransaction(TransactionRequestDto dto) {
    return new Transaction(
        UUID.randomUUID(),
        dto.type(),
        dto.description(),
        dto.amount().setScale(2, RoundingMode.HALF_UP),
        dto.occurredAt().map(OffsetDateTime::toInstant).orElse(Instant.now(clock)));
  }

  private TransactionResponseDto toResponseDto(Transaction transaction) {
    return new TransactionResponseDto(
        transaction.getId(),
        transaction.getType(),
        transaction.getDescription(),
        transaction.getAmount(),
        transaction.getOccurredAt().atOffset(UTC));
  }
}
