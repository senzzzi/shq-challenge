package com.senzzzi.shq.service;

import com.senzzzi.shq.exception.QuoteDoesNotExistException;
import com.senzzzi.shq.model.CoinValue;
import com.senzzzi.shq.model.PurchaseDTO;
import com.senzzzi.shq.model.persistence.CoinEntity;
import com.senzzzi.shq.model.persistence.QuoteEntity;
import com.senzzzi.shq.repository.CoinRepository;
import com.senzzzi.shq.repository.QuoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StateServiceTest {

    private StateService target;

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private CoinRepository coinRepository;

    @BeforeEach
    public void setup() {
        if (target == null) {
            target = new StateService(requireNonNull(quoteRepository), requireNonNull(coinRepository));
        }

        target.cancel();
    }

    @Test
    public void testBuyQuoteSuccess() {

        when(quoteRepository.findById(1L)).thenReturn(Optional.of(QuoteEntity.builder()
                .id(1L)
                .quote("beautiful day")
                .price(100)
                .build()));

        PurchaseDTO purchaseDTO = target.startBuying(1L);

        assertEquals(1L, purchaseDTO.getQuote().getId());
        assertEquals(100, purchaseDTO.getQuote().getPrice());
        assertEquals("beautiful day", purchaseDTO.getQuote().getQuote());
        assertTrue(purchaseDTO.getCoins().isEmpty());
        assertEquals(100, purchaseDTO.getRemainingMoney());
    }

    @Test
    public void testBuyWrongQuote() {
        when(quoteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(QuoteDoesNotExistException.class, () -> target.startBuying(1L));
    }

    @Test
    public void testAddCoinQuote() {
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(QuoteEntity.builder()
                .id(1L)
                .quote("beautiful day")
                .price(100)
                .build()));

        target.startBuying(1L);

        PurchaseDTO purchaseDTO = target.addCoin(CoinValue.COIN_10);

        assertEquals(1L, purchaseDTO.getQuote().getId());
        assertEquals(100, purchaseDTO.getQuote().getPrice());
        assertEquals("beautiful day", purchaseDTO.getQuote().getQuote());
        assertTrue(purchaseDTO.getCoins().containsKey(CoinValue.COIN_10));
        assertEquals(1, purchaseDTO.getCoins().get(CoinValue.COIN_10));
        assertEquals(90, purchaseDTO.getRemainingMoney());
    }

    @Test
    public void testAddCoinEnoughToBuyQuote() {

        mockCoins();

        when(quoteRepository.findById(1L)).thenReturn(Optional.of(QuoteEntity.builder()
                .id(1L)
                .quote("beautiful day")
                .price(75)
                .build()));

        target.startBuying(1L);

        PurchaseDTO purchaseDTO = target.addCoin(CoinValue.COIN_100);
        assertTrue(purchaseDTO.getRemainingMoney() < 0);

        purchaseDTO = target.finalizePurchase();

        assertEquals(1L, purchaseDTO.getQuote().getId());
        assertEquals(75, purchaseDTO.getQuote().getPrice());
        assertEquals("beautiful day", purchaseDTO.getQuote().getQuote());
        // assert change is correct
        assertTrue(purchaseDTO.getCoins().containsKey(CoinValue.COIN_25));
        assertEquals(1, purchaseDTO.getCoins().get(CoinValue.COIN_25));
        assertEquals(0, purchaseDTO.getRemainingMoney());

        // quote and coins updated
        verify(quoteRepository, times(1)).save(any());
        verify(coinRepository, times(5)).save(any());
    }

    @Test
    public void testCancel() {
        when(quoteRepository.findById(1L)).thenReturn(Optional.of(QuoteEntity.builder()
                .id(1L)
                .quote("beautiful day")
                .price(75)
                .build()));

        target.startBuying(1L);

        target.addCoin(CoinValue.COIN_25);

        PurchaseDTO purchaseDTO = target.cancel();

        // assert give back money is correct
        assertTrue(purchaseDTO.getCoins().containsKey(CoinValue.COIN_25));
        assertEquals(1, purchaseDTO.getCoins().get(CoinValue.COIN_25));
        assertNull(purchaseDTO.getRemainingMoney());

    }

    private void mockCoins() {
        when(coinRepository.findAll()).thenReturn(List.of(CoinEntity.builder()
                        .id(1L)
                        .value(CoinValue.COIN_10)
                        .count(10)
                        .build(),
                CoinEntity.builder()
                        .id(2L)
                        .value(CoinValue.COIN_50)
                        .count(10)
                        .build(),
                CoinEntity.builder()
                        .id(2L)
                        .value(CoinValue.COIN_100)
                        .count(10)
                        .build(),
                CoinEntity.builder()
                        .id(2L)
                        .value(CoinValue.COIN_25)
                        .count(10)
                        .build()));
    }

}
