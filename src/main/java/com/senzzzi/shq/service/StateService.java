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
        // TODO: implement this
        Map<CoinValue, Integer> change = getChange(buyingQuote.getPrice() - coinCount.entrySet()
                .stream().mapToInt((e) -> e.getKey().getValue() * e.getValue()).sum());

        // return product
        PurchaseDTO purchaseDTO = PurchaseDTO.builder()
                .quote(buyingQuote)
                .coins(change)
                .remainingMoney(0)
                .build();

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

    private Map<CoinValue, Integer> getChange(int i) {
    }

    private void resetPurchaseState() {
        buyingQuote = null;
        coinCount = new HashMap<>();
    }
}
