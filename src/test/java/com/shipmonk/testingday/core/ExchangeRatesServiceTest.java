package com.shipmonk.testingday.core;

import com.shipmonk.testingday.core.entity.Rates;
import com.shipmonk.testingday.core.ports.ExchangeRates;
import com.shipmonk.testingday.core.ports.ExchangeRatesException;
import com.shipmonk.testingday.core.ports.ExchangeRatesRepository;
import com.shipmonk.testingday.core.ports.RatesDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ExchangeRatesServiceTest {

    @Autowired
    private ExchangeRatesService exchangeRatesService;

    @MockBean
    private ExchangeRatesRepository repository;

    @MockBean
    private ExchangeRates exchangeRates;

    private final LocalDate now = LocalDate.now();
    private final RatesDTO ratesDTO = new RatesDTO(
        now,
        Instant.now().getEpochSecond(),
        Map.of()
    );

    @Test
    void loadRates_whenDateIsBefore1999_raiseException() {
        DateOutOfRange exception = assertThrows(DateOutOfRange.class, () -> exchangeRatesService.loadRates(LocalDate.of(1998, 12, 31)));
        assertEquals("Date 1998-12-31 is before year 1999", exception.getMessage());
    }

    @Test
    void loadRates_whenDateIsAfterToday_raiseException() {
        DateOutOfRange exception = assertThrows(DateOutOfRange.class, () -> exchangeRatesService.loadRates(LocalDate.of(2999, 12, 31)));
        assertEquals("Date 2999-12-31 is in the future", exception.getMessage());
    }


    @Test
    void loadRates_whenRepositoryHasValue_returnsThatValue() throws DateOutOfRange, ExchangeRatesException {
        Rates rates = new Rates(
            now,
            Instant.now().getEpochSecond(),
            Map.of()
        );

        Mockito.when(repository.findByDate(now)).thenReturn(Optional.of(rates));

        Rates actual = exchangeRatesService.loadRates(now);

        assertEquals(rates, actual);
        Mockito.verifyNoInteractions(exchangeRates);
        Mockito.verify(repository).findByDate(now);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void loadRates_whenRepositoryDoesntHaveValue_callsExchangeRates() throws DateOutOfRange, ExchangeRatesException {
        Mockito.when(repository.findByDate(now)).thenReturn(Optional.empty());
        Mockito.when(exchangeRates.loadRates(now)).thenReturn(ratesDTO);

        exchangeRatesService.loadRates(now);
        Mockito.verify(exchangeRates).loadRates(now);
        Mockito.verify(repository).findByDate(now);
    }

    @Test
    void loadRates_whenExchangeRatesRaisesException_raiseTheException() throws ExchangeRatesException {
        Mockito.when(repository.findByDate(now)).thenReturn(Optional.empty());
        Mockito.when(exchangeRates.loadRates(now)).thenThrow(ExchangeRatesException.class);

        assertThrows(ExchangeRatesException.class, () -> exchangeRatesService.loadRates(now));
        Mockito.verify(repository).findByDate(now);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void loadRates_whenRepositoryDoesntHaveValue_saveNewValueToRepositoryAndReturns() throws DateOutOfRange, ExchangeRatesException {

        Map<String, Double> expectedRates = Map.of(
            "CZK", 10000000.0
        );
        long expectedEpoch = Instant.now().getEpochSecond();
        RatesDTO ratesDTO = new RatesDTO(
            now,
            expectedEpoch,
            expectedRates
        );
        Mockito.when(repository.findByDate(now)).thenReturn(Optional.empty());
        Mockito.when(exchangeRates.loadRates(now)).thenReturn(ratesDTO);
        Rates expected = new Rates(now, expectedEpoch, expectedRates);
//        Rates expected = new Rates(now, expectedEpoch);
        Mockito.when(repository.save(expected)).thenReturn(expected);

        Rates actual = exchangeRatesService.loadRates(now);
        Mockito.verify(repository).save(expected);
        assertEquals(expected, actual);

        Mockito.verify(repository).findByDate(now);
        Mockito.verify(exchangeRates).loadRates(now);
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoMoreInteractions(exchangeRates);
    }

}

