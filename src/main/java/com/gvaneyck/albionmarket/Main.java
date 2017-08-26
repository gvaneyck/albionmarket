package com.gvaneyck.albionmarket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class Main {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Map<String, Object> config = objectMapper.readValue(Files.readAllBytes(new File("config.json").toPath()), Map.class);

            String recipeContent = new String(Files.readAllBytes(new File("recipes.json").toPath()), "UTF-8");
            List<Recipe> recipes = objectMapper.readValue(recipeContent, new TypeReference<List<Recipe>>() {});

            File[] files = new File((String)config.get("packetDir")).listFiles();
            File theFile = files[files.length - 1];
            String[] lines = new String(Files.readAllBytes(theFile.toPath()), "UTF-8").split("\n");

            Market market = new Market((String)config.get("me"));
            for (String line : lines) {
                try {
                    MarketEntryRaw raw = objectMapper.readValue(line, MarketEntryRaw.class);
                    market.addEntry(raw);
                } catch (Exception e) {
                    // Expected, sometimes the packet reader dumps bad data
                }
            }

            System.out.println("Recipes");
            for (Recipe recipe : recipes) {
                recipe.getMaxBuy(market, -0.05);
            }
            System.out.println("");

            System.out.println("Fame");
            for (Recipe recipe : recipes) {
                recipe.getFameForMargin(market, -0.13);
            }
            System.out.println("");

            System.out.println("Focus Recipes");
            Map<String, Map<String, Integer>> masteries = (Map<String, Map<String, Integer>>)config.get("masteries");
            for (Recipe recipe : recipes) {
                recipe.getFocusProfit(market, getFocusEfficiency(masteries, recipe.getOutput()));
            }
            System.out.println("");

            System.out.println("Sell Adjustments");
            market.checkPriceDecreases();
            System.out.println("");

            System.out.println("Black Market");
            market.scanBlackMarket(10000);
            System.out.println("");

            market.dumpLowestPrices();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double getFocusEfficiency(Map<String, Map<String, Integer>> masteries, String item) {
        double total = 0;
        String tier = item.substring(1, 2);
        String name = item.substring(3, item.indexOf("_", 3));

        total += masteries.get(name).get("4") * 0.3;
        total += masteries.get(name).get("5") * 0.3;
        total += masteries.get(name).get("6") * 0.3;
        total += masteries.get(name).get("7") * 0.3;
        total += masteries.get(name).get("8") * 0.3;

        if (masteries.get(name).containsKey(tier)) {
            total += masteries.get(name).get(tier) * 2.5;
        }

        return total;
    }
}
