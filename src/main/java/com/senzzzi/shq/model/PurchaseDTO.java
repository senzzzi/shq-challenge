package com.senzzzi.shq.model;

import com.senzzzi.shq.model.persistence.QuoteEntity;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class PurchaseDTO {

    private QuoteEntity quote;
    private Integer remainingMoney;
    private Map<CoinValue, Integer> coins;

}
