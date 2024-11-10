package com.shipmonk.testingday.core;

import com.shipmonk.testingday.core.entity.Rates;
import com.shipmonk.testingday.core.ports.ExchangeRates;
import com.shipmonk.testingday.core.ports.ExchangeRatesException;
import com.shipmonk.testingday.core.ports.ExchangeRatesRepository;
import com.shipmonk.testingday.core.ports.RatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class ExchangeRatesService {
    private final ExchangeRatesRepository repository;
    private final ExchangeRates exchangeRates;

    @Autowired
    public ExchangeRatesService(ExchangeRatesRepository repository, ExchangeRates exchangeRates) {
        this.repository = repository;
        this.exchangeRates = exchangeRates;
    }

//    @Transactional
    public Rates loadRates(LocalDate date) throws DateOutOfRange, ExchangeRatesException {
        if (date.isBefore(LocalDate.of(1999, 1, 1))) {
            throw new DateOutOfRange(String.format("Date %s is before year 1999", date));
        }
        if (date.isAfter(LocalDate.now())) {
            throw new DateOutOfRange(String.format("Date %s is in the future", date));
        }

        Optional<Rates> optionalRates = repository.findByDate(date);
        if (optionalRates.isPresent()) {
            return optionalRates.get();
        } else {
            RatesDTO loaded = exchangeRates.loadRates(date);
            System.out.println(loaded);
            return repository.save(new Rates(
                date,
                loaded.collectedTimestamp(),
                loaded.rates()
            ));
        }
    }
}

