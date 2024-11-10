package com.shipmonk.testingday.adapters;

import com.shipmonk.testingday.core.ports.ExchangeRates;
import com.shipmonk.testingday.core.ports.ExchangeRatesException;
import com.shipmonk.testingday.core.ports.RatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.Map;

@lombok.extern.log4j.Log4j2
@Component
public class FixerExchangeRates implements ExchangeRates {
    private final RestClient restClient;
    private final String apiKey;

    private record Error(
        int code,
        String type,
        String info
    ) {
    }

    private record FixerResponse(
        String success,
        Error error,
        long timestamp,
        String historical,
        String base,
        LocalDate date,
        Map<String, Double> rates
    ) {
    }

    @Autowired
    public FixerExchangeRates(
        RestClient.Builder builder,

        @Value("${fixer.api.base-url}")
        String baseUrl,

        @Value("${fixer.api.key}")
        String apiKey) {
        restClient = builder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    @Override
    public RatesDTO loadRates(LocalDate date) throws ExchangeRatesException {
        log.info("Loading fixer exchange rates for {}", date);
        RestClient.RequestHeadersSpec<?> a = restClient
            .get()
            .uri(
                (builder) -> builder
                    .path(date.toString())
                    .queryParam("access_key", apiKey)
                    .queryParam("base", "USD")
                    .build());

        FixerResponse response = a.retrieve().body(FixerResponse.class);

        if (response.error != null) {
            log.error("Loading fixer exchange rates error: {}", response.error);
            throw new ExchangeRatesException(
                String.format(
                    "Failed to get response from Fixer. %s: %s",
                    response.error().type(),
                    response.error().info()
                ));
        }

        return new RatesDTO(
            date,
            response.timestamp(),
            response.rates()
        );
    }
}
