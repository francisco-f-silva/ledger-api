package io.github.francisco_f_silva.ledger_api.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(
    name = "Transactions",
    description =
        "Handles deposits, withdrawals, and related balance.")
public class TransactionController {
  @GetMapping("/test")
  @Operation(summary = "Does something", description = "Does something more descriptively.")
  public String testEndpoint(@RequestParam(defaultValue = "World") String name) {
    return String.format("Hello %s\n", name);
  }
}
