package com.shipmonk.testingday.controller;

import com.shipmonk.testingday.core.DateOutOfRange;
import com.shipmonk.testingday.core.ExchangeRatesService;
import com.shipmonk.testingday.core.ports.ExchangeRatesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping(
    path = "/api/v1/rates"
)
@lombok.extern.log4j.Log4j2
public class ExchangeRatesController {

    private final ExchangeRatesService exchangeRatesService;

    @Autowired
    public ExchangeRatesController(ExchangeRatesService exchangeRatesService) {
        this.exchangeRatesService = exchangeRatesService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{day}")
    public ResponseEntity<Object> getRates(@PathVariable("day") LocalDate day) throws ExchangeRatesException, DateOutOfRange {
        log.debug("New request for day {}", day);
        return new ResponseEntity<>(
            exchangeRatesService.loadRates(day),
            HttpStatus.OK
        );
    }

    @ExceptionHandler
    public ResponseEntity<String> handleDateRangeException(DateOutOfRange exception) {
        log.debug("Invalid Date Range: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleRatesException(ExchangeRatesException exception) {
        log.error("Failed to load rates: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(exception.getMessage());
    }


}
