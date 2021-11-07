package com.senzzzi.shq.service;

import com.senzzzi.shq.exception.QuoteDoesNotExistException;
import com.senzzzi.shq.model.CoinValue;
import com.senzzzi.shq.model.PurchaseDTO;
import com.senzzzi.shq.model.persistence.CoinEntity;
import com.senzzzi.shq.model.persistence.QuoteEntity;
import com.senzzzi.shq.repository.CoinRepository;
import com.senzzzi.shq.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StateService {

    private final QuoteRepository quoteRepository;
    private final CoinRepository coinRepository;

    private QuoteEntity buyingQuote = null;
    private Map<CoinValue, Integer> coinCount = new HashMap<>();

    public PurchaseDTO startBuying(Long quoteId) {
       Optional<QuoteEntity> quoteEntityOptional = quoteRepository.findById(quoteId);
       if (quoteEntityOptional.isPresent()) {
            buyingQuote = quoteEntityOptional.get();
       } else {
            throw new QuoteDoesNotExistException("Quote does not exist");
       }

       return PurchaseDTO.builder()
               .quote(buyingQuote)
               .remainingMoney(buyingQuote.getPrice() - coinCount.entrySet().stream().mapToInt((e) -> e.getKey().getValue() * e.getValue()).sum())
               .coins(coinCount)
               .build();
    }

    public PurchaseDTO addCoin(CoinValue coinValue) {
        if (buyingQuote != null) {
            coinCount.putIfAbsent(coinValue, 0);
            coinCount.put(coinValue, coinCount.get(coinValue) + 1);
        }

        return PurchaseDTO.builder()
                .quote(buyingQuote)
                .remainingMoney(buyingQuote.getPrice() - coinCount.entrySet().stream().mapToInt((e) -> e.getKey().getValue() * e.getValue()).sum())
                .coins(coinCount)
                .build();
    }

    @Transactional
    // transactional is important to store money and mark item as bought
    public PurchaseDTO finalizePurchase() {

        // mark quote as bought
        buyingQuote.setAvailable(false);
        quoteRepository.save(buyingQuote);

        // add coins to DB
        List<CoinEntity> coinEntityList = (List<CoinEntity>) coinRepository.findAll();
        coinEntityList.forEach(coin -> {
            if (coinCount.containsKey(coin.getValue())) {
                coin.setCount(coin.getCount() + coinCount.get(coin.getValue()));
                coinRepository.save(coin);
            }
        });

        // calculate change
        Map<CoinValue, Integer> change = getChange(Math.abs(buyingQuote.getPrice() - coinCount.entrySet()
                .stream().mapToInt((e) -> e.getKey().getValue() * e.getValue()).sum()),
                coinEntityList);

        // update coin count with change returns
        coinEntityList.forEach(coin -> {
            if (change.containsKey(coin.getValue())) {
                coin.setCount(coin.getCount() - change.get(coin.getValue()));
                coinRepository.save(coin);
            }
        });

        // return product
        PurchaseDTO purchaseDTO = PurchaseDTO.builder()
                .quote(buyingQuote)
                .coins(change)
                .remainingMoney(0)
                .build();

        // reset state to allow a different purchase
        resetPurchaseState();
        return purchaseDTO;
    }

    public PurchaseDTO cancel() {
        Map<CoinValue, Integer> coinsToGiveBack = coinCount;
        resetPurchaseState();
        return PurchaseDTO.builder()
                .coins(coinsToGiveBack)
                .build();
    }

    private Map<CoinValue, Integer> getChange(int totalChangeInCents, List<CoinEntity> coinEntities) {
        Map<CoinValue, Integer> change = new HashMap<>();

        coinEntities = coinEntities.stream().sorted((c1, c2) -> Integer.compare(c2.getValue().getValue(), c1.getValue().getValue())).collect(Collectors.toList());

        for (CoinEntity coin : coinEntities) {

            int numberOfCoins = totalChangeInCents / coin.getValue().getValue();

            if (numberOfCoins > coin.getCount()) {
                numberOfCoins = coin.getCount();
            }

            totalChangeInCents = totalChangeInCents - (numberOfCoins * coin.getValue().getValue());

            change.put(coin.getValue(), numberOfCoins);

        }

        return change;
    }

    private void resetPurchaseState() {
        buyingQuote = null;
        coinCount = new HashMap<>();
    }
}
