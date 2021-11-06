package com.senzzzi.shq.service;

import com.senzzzi.shq.model.CoinValue;
import com.senzzzi.shq.model.persistence.CoinEntity;
import com.senzzzi.shq.model.persistence.QuoteEntity;
import com.senzzzi.shq.repository.CoinRepository;
import com.senzzzi.shq.repository.QuoteRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class InitialDataService {

    private CoinRepository coinRepository;
    private QuoteRepository quoteRepository;

    public InitialDataService(CoinRepository coinRepository, QuoteRepository quoteRepository) {
        this.coinRepository = coinRepository;
        this.quoteRepository = quoteRepository;
        populateInitialData();
    }

    @Transactional
    public void populateInitialData() {

        if (coinRepository.count() == 0) {
            this.coinRepository.saveAll(List.of(
                    CoinEntity.builder().value(CoinValue.COIN_1).count(25).build(),
                    CoinEntity.builder().value(CoinValue.COIN_5).count(25).build(),
                    CoinEntity.builder().value(CoinValue.COIN_10).count(25).build(),
                    CoinEntity.builder().value(CoinValue.COIN_25).count(25).build(),
                    CoinEntity.builder().value(CoinValue.COIN_50).count(25).build(),
                    CoinEntity.builder().value(CoinValue.COIN_100).count(25).build()
            ));
        }

        if (quoteRepository.count() == 0) {
            this.quoteRepository.saveAll(List.of(
                    QuoteEntity.builder().quote("When you have a dream, you’ve got to grab it and never let go.").price(50).available(true).build(),
                    QuoteEntity.builder().quote("Nothing is impossible. The word itself says ‘I’m possible!").price(60).available(true).build(),
                    QuoteEntity.builder().quote("There is nothing impossible to they who will try.").price(75).available(true).build(),
                    QuoteEntity.builder().quote("The bad news is time flies. The good news is you’re the pilot.").price(80).available(true).build(),
                    QuoteEntity.builder().quote("Life has got all those twists and turns. You’ve got to hold on tight and off you go.").price(100).available(true).build()
            ));
        }
    }

    public void deleteAllData() {
        this.coinRepository.deleteAll();
        this.quoteRepository.deleteAll();
    }
}
