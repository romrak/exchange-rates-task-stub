package com.shipmonk.testingday.core.ports;

import com.shipmonk.testingday.core.entity.Rates;
import org.springframework.data.repository.Repository;

import java.time.LocalDate;
import java.util.Optional;

public interface ExchangeRatesRepository extends Repository<Rates, LocalDate> {
    Optional<Rates> findByDate(LocalDate date);

    Rates save(Rates rates);
}
