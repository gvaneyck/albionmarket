package com.gvaneyck.albionmarket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BlackMarketOption implements Comparable<BlackMarketOption> {

    private String item;
    private long qty;
    private long requestPrice;
    private long offerPrice;

    private long getTotalValue() {
        return qty * (requestPrice - offerPrice);
    }

    @Override
    public int compareTo(BlackMarketOption o) {
        return -Long.compare(getTotalValue(), o.getTotalValue());
    }

    @Override
    public String toString() {
        return String.format("%s - %d = %dx %d --- %d -> %d",
                item,
                qty * (requestPrice - offerPrice),
                qty,
                requestPrice - offerPrice,
                offerPrice,
                requestPrice);
    }
}
