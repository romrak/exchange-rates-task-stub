package com.shipmonk.testingday.core.ports;

import java.time.LocalDate;
import java.util.Map;

public record RatesDTO(
    LocalDate date,
    long collectedTimestamp,
    Map<String, Double> rates
) {

}
