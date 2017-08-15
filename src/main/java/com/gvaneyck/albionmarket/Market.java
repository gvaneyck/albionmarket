package com.gvaneyck.albionmarket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Market {

    private Map<Long, MarketEntry> ids = new LinkedHashMap<>();
    private Map<String, List<MarketEntry>> offers = new LinkedHashMap<>();
    private Map<String, List<MarketEntry>> requests = new LinkedHashMap<>();

    public void addEntry(MarketEntryRaw raw) {
        addEntry(raw.getAuctionType(), raw.getId(), raw.getFullType(), raw.getPrice(), raw.getQty());
    }

    public void addEntry(String auctionType, long id, String item, long price, long qty) {
        if (ids.containsKey(id)) {
            ids.get(id).setItem(item);
            ids.get(id).setPrice(price);
            ids.get(id).setQty(qty);
        } else {
            if (auctionType.equals("offer")) {
                if (!offers.containsKey(item)) {
                    offers.put(item, new ArrayList<>());
                }
                offers.get(item).add(new MarketEntry(id, item, price, qty));
                Collections.sort(offers.get(item));
            } else if (auctionType.equals("request")) {
                if (!requests.containsKey(item)) {
                    requests.put(item, new ArrayList<>());
                }
                requests.get(item).add(new MarketEntry(id, item, price, qty));
                Collections.sort(requests.get(item));
            }
        }
    }

    public MarketEntry getCheapest(String item) {
        return getCheapest(item, 0);
    }

    public MarketEntry getCheapest(String item, int idx) {
        List<MarketEntry> entries = offers.get(item);
        if (entries == null) {
            return null;
        }

        return entries.get(idx);
    }

    public void scanBlackMarket() {
        for (String item : requests.keySet()) {
            if (offers.containsKey(item)) {
                List<MarketEntry> request = requests.get(item);
                MarketEntry requestItem = request.get(request.size() - 1);
                long requestPrice = requestItem.getPrice();
                List<MarketEntry> offer = offers.get(item);
                long offerPrice = offer.get(0).getPrice();
                if (requestPrice > offerPrice) {
                    System.out.println(String.format("%s - %d = %dx %d --- %d -> %d",
                            item,
                            requestItem.getQty() * (requestPrice - offerPrice),
                            requestItem.getQty(),
                            requestPrice - offerPrice,
                            offerPrice,
                            requestPrice));
                }
            }
        }
    }
}
