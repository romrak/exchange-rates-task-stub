package com.shipmonk.testingday.core.ports;

import com.shipmonk.testingday.core.entity.Rates;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
class ExchangeRatesRepositoryTest {

    @Autowired
    private ExchangeRatesRepository exchangeRatesRepository;

    @Test
    void findByDate() {
        Optional<Rates> response = exchangeRatesRepository.findByDate(LocalDate.now());
    }

    @Test
    void save() {
    }
}
