package com.shipmonk.testingday.core.ports;

import java.time.LocalDate;

public interface ExchangeRates  {
    RatesDTO loadRates(LocalDate date) throws ExchangeRatesException;
}

