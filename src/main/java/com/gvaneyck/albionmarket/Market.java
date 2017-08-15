package com.gvaneyck.albionmarket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Market {

    private String me;

    private Map<Long, MarketEntry> ids = new LinkedHashMap<>();
    private Map<String, List<MarketEntry>> offers = new LinkedHashMap<>();
    private Map<String, List<MarketEntry>> requests = new LinkedHashMap<>();

    public Market(String me) {
        this.me = me;
    }

    public void addEntry(MarketEntryRaw raw) {
        addEntry(raw.getSellerName(), raw.getAuctionType(), raw.getId(), raw.getFullType(), raw.getPrice(), raw.getQty());
    }

    public void addEntry(String seller, String auctionType, long id, String item, long price, long qty) {
        if (ids.containsKey(id)) {
            ids.get(id).setItem(item);
            ids.get(id).setPrice(price);
            ids.get(id).setQty(qty);
            ids.get(id).setSeller(seller);
        } else {
            if (auctionType.equals("offer")) {
                if (!offers.containsKey(item)) {
                    offers.put(item, new ArrayList<>());
                }
                offers.get(item).add(new MarketEntry(id, item, price, qty, seller));
                Collections.sort(offers.get(item));
            } else if (auctionType.equals("request")) {
                if (!requests.containsKey(item)) {
                    requests.put(item, new ArrayList<>());
                }
                requests.get(item).add(new MarketEntry(id, item, price, qty, seller));
                Collections.sort(requests.get(item));
            }
        }
    }

    public MarketEntry getEntry(String item) {
        return getEntry(item, 0, false);
    }

    public MarketEntry getEntry(String item, int idx) {
        return getEntry(item, idx, false);
    }

    public MarketEntry getEntry(String item, int idx, boolean safe) {
        List<MarketEntry> entries = offers.get(item);
        if (entries == null) {
            return null;
        }

        if (safe && idx >= entries.size()) {
            return entries.get(entries.size() - 1);
        } else {
            return entries.get(idx);
        }
    }

    public void scanBlackMarket() {
        List<BlackMarketOption> options = new ArrayList<>();
        for (String item : requests.keySet()) {
            if (offers.containsKey(item)) {
                List<MarketEntry> request = requests.get(item);
                MarketEntry requestItem = request.get(request.size() - 1);
                long requestPrice = requestItem.getPrice();
                List<MarketEntry> offer = offers.get(item);
                long offerPrice = offer.get(0).getPrice();
                if (requestPrice > offerPrice) {
                    options.add(new BlackMarketOption(item, requestItem.getQty(), requestPrice, offerPrice));
                }
            }
        }

        Collections.sort(options);
        for (BlackMarketOption option : options) {
            System.out.println(option);
        }
    }

    public void checkPriceDecreases() {
        for (String item : offers.keySet()) {
            long minPrice = offers.get(item).get(0).getPrice();
            long myPrice = 0;
            long total = 0;
            boolean hasMe = false;
            for (MarketEntry entry : offers.get(item)) {
                if (!hasMe) {
                    if (entry.getSeller().equals(me)) {
                        hasMe = true;
                        myPrice = entry.getPrice();
                    } else {
                        total += entry.getQty();
                    }
                }
            }

            if (hasMe) {
                System.out.println(String.format("%s - %d -> %d @ %d",
                        item, myPrice, minPrice, total));
            }
        }
    }
}
