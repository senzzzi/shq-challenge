package com.senzzzi.shq.model;

import com.senzzzi.shq.exception.CoinValueDoesNotExistException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoinValue {

    COIN_1(1),
    COIN_5(5),
    COIN_10(10),
    COIN_25(25),
    COIN_50(50),
    COIN_100(100);

    private int value;

    public static CoinValue fromValue(Integer value) {
        for (CoinValue cv : CoinValue.values()) {
            if (value == cv.getValue()) {
                return cv;
            }
        }
        throw new CoinValueDoesNotExistException("Invalid Coin Value");
    }
}
