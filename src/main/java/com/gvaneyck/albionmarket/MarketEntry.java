package com.gvaneyck.albionmarket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MarketEntry implements Comparable<MarketEntry> {

    private long id;

    private String item;
    private long price;
    private long qty;

    private String seller;

    public int compareTo(MarketEntry o) {
        int result = Long.compare(price, o.getPrice());
        if (result == 0) {
            result = -Long.compare(qty, o.getQty());
        }
        return result;
    }
}
