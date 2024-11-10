package com.shipmonk.testingday.adapters;

import com.shipmonk.testingday.core.ports.ExchangeRates;
import com.shipmonk.testingday.core.ports.RatesDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

@Component
public class FixerExchangeRates implements ExchangeRates {
    @Override
    public RatesDTO loadRates(LocalDate date) {
        return new RatesDTO(
            date,
            Instant.now().getEpochSecond(),
            Map.of("HELLO", 123.987)
        );
    }
}
