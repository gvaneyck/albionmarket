package com.gvaneyck.albionmarket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Recipe {

    private String output;
    private Map<String, Integer> inputs;

    public Recipe(String output, String... inputs) {
        this.output = output;
        this.inputs = new LinkedHashMap<>();
        for (int i = 0; i < inputs.length; i += 2) {
            this.inputs.put(inputs[i], Integer.parseInt(inputs[i + 1]));
        }
    }

    @Override
    public String toString() {
        return output;
    }

    public long getCost(Market market) {
        long total = 0;
        for (String input : inputs.keySet()) {
            total += market.getCheapest(input).getPrice() * inputs.get(input);
        }
        return total;
    }

    public void getMaxBuy(Market market, double margin) {
        Map<String, Integer> lastIdxs = new LinkedHashMap<>();
        Map<String, Integer> idxs = new LinkedHashMap<>();
        Map<String, Long> qtys = new LinkedHashMap<>();
        Map<String, Long> prices = new LinkedHashMap<>();

        for (String input : inputs.keySet()) {
            idxs.put(input, 0);
            qtys.put(input, 0L);
            prices.put(input, 0L);
        }

        long expectedSell;
        try {
            expectedSell = market.getCheapest(output).getPrice() - 1;
        } catch (NullPointerException e) {
            return;
        }

        double maxPrice = expectedSell * (1 - margin);
        long totalProfit = 0;
        long totalCost = 0;
        long qty = 0;
        try {
            long nextCost = getNextCost(market, idxs, qtys, prices);
            if (nextCost >= maxPrice) {
                return;
            }
            while (nextCost < maxPrice) {
                qty++;
                for (String input : inputs.keySet()) {
                    lastIdxs.put(input, idxs.get(input));
                }
                totalProfit += (long) (maxPrice - nextCost);
                totalCost += nextCost;
                nextCost = getNextCost(market, idxs, qtys, prices);
            }
        } catch (NullPointerException e) {
            // Missing market data
//            System.err.println("Missing market data for " + output);
            return;
        } catch (Exception e) {
            // We could out of bounds on getNextCost, assume we should just buy the entire market
        }

        System.out.println(String.format("%s - %d for %.1f%% (%d), sold for %d, invest %d", output, qty, (100f * totalProfit / totalCost), totalProfit, expectedSell, totalCost));
        for (String input : inputs.keySet()) {
            System.out.println(String.format("%s - %d",
                    input,
                    market.getCheapest(input, lastIdxs.get(input)).getPrice()));
        }
        System.out.println();
    }

    private long getNextCost(Market market, Map<String, Integer> idxs, Map<String, Long> qtys, Map<String, Long> prices) {
        long total = 0;
        for (String input : inputs.keySet()) {
            long qty = inputs.get(input);

            long tempQty = Math.min(qty, qtys.get(input));
            total += tempQty * prices.get(input);
            qty -= tempQty;
            qtys.put(input, qtys.get(input) - tempQty);

            while (qty != 0) {
                int idx = idxs.get(input);
                idxs.put(input, idx + 1);

                MarketEntry entry = market.getCheapest(input, idx);
                prices.put(input, entry.getPrice());
                qtys.put(input, entry.getQty());

                tempQty = Math.min(qty, qtys.get(input));
                total += tempQty * prices.get(input);
                qty -= tempQty;
                qtys.put(input, qtys.get(input) - tempQty);
            }
        }
        return total;
    }
}
