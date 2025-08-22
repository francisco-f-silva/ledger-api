package io.github.francisco_f_silva.ledger_api.web;

import io.github.francisco_f_silva.ledger_api.model.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransactionResponseDto(
    UUID id,
    TransactionType type,
    String description,
    BigDecimal amount,
    OffsetDateTime occurredAt) {}
