package com.shipmonk.testingday.core;

import com.shipmonk.testingday.core.entity.Rates;
import com.shipmonk.testingday.core.ports.ExchangeRates;
import com.shipmonk.testingday.core.ports.ExchangeRatesException;
import com.shipmonk.testingday.core.ports.ExchangeRatesRepository;
import com.shipmonk.testingday.core.ports.RatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@lombok.extern.log4j.Log4j2
public class ExchangeRatesService {
    private final ExchangeRatesRepository repository;
    private final ExchangeRates exchangeRates;

    @Autowired
    public ExchangeRatesService(ExchangeRatesRepository repository, ExchangeRates exchangeRates) {
        this.repository = repository;
        this.exchangeRates = exchangeRates;
    }

    @Transactional
    public Rates loadRates(LocalDate date) throws DateOutOfRange, ExchangeRatesException {
        log.debug("Loading exchange rates for {}", date);
        if (date.isBefore(LocalDate.of(1999, 1, 1))) {
            throw new DateOutOfRange(String.format("Date %s is before year 1999", date));
        }
        if (date.isAfter(LocalDate.now())) {
            throw new DateOutOfRange(String.format("Date %s is in the future", date));
        }

        Optional<Rates> optionalRates = repository.findByDate(date);
        if (optionalRates.isPresent()) {
            log.info("Found exchange rates for {}", date);
            return optionalRates.get();
        } else {
            log.info("Rates not found for {}", date);
            RatesDTO loaded = exchangeRates.loadRates(date);
            return repository.save(new Rates(
                date,
                loaded.collectedTimestamp(),
                loaded.rates()
            ));
        }
    }
}

