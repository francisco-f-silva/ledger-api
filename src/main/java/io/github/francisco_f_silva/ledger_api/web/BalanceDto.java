package io.github.francisco_f_silva.ledger_api.web;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BalanceDto(@NotNull BigDecimal balance) {}
