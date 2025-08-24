package io.github.francisco_f_silva.ledger_api.web;

import io.github.francisco_f_silva.ledger_api.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponseDto(
    @NotNull UUID id,
    @NotNull TransactionType type,
    @NotBlank String description,
    @NotNull @Positive BigDecimal amount,
    @NotNull Instant occurredAt) {}
