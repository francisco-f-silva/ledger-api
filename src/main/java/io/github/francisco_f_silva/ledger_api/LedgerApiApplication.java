package io.github.francisco_f_silva.ledger_api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
public class LedgerApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(LedgerApiApplication.class, args);
  }

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Tiny Ledger API")
                .version("1.0")
                .description(
                    "A small REST API to record deposits and withdrawals, view transaction history, and check balance."));
  }
}
