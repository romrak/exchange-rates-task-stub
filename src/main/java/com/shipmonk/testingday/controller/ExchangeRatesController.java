package com.shipmonk.testingday.controller;

import com.shipmonk.testingday.core.DateOutOfRange;
import com.shipmonk.testingday.core.ExchangeRatesService;
import com.shipmonk.testingday.core.ports.ExchangeRatesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(
    path = "/api/v1/rates"
)
public class ExchangeRatesController {

    private final ExchangeRatesService exchangeRatesService;

    @Autowired
    public ExchangeRatesController(ExchangeRatesService exchangeRatesService) {
        this.exchangeRatesService = exchangeRatesService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{day}")
    public ResponseEntity<Object> getRates(@PathVariable("day") String day) throws ExchangeRatesException, DateOutOfRange {
        return new ResponseEntity<>(
            exchangeRatesService.loadRates(LocalDate.parse(day)),
            HttpStatus.OK
        );
    }

}
