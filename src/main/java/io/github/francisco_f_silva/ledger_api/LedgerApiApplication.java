package io.github.francisco_f_silva.ledger_api;

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
}
