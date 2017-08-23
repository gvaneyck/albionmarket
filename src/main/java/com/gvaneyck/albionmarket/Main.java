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
            Map<String, String> config = objectMapper.readValue(Files.readAllBytes(new File("config.json").toPath()), Map.class);

            String recipeContent = new String(Files.readAllBytes(new File("recipes.json").toPath()), "UTF-8");
            List<Recipe> recipes = objectMapper.readValue(recipeContent, new TypeReference<List<Recipe>>() {
            });

            File[] files = new File(config.get("packetDir")).listFiles();
            File theFile = files[files.length - 1];
            String[] lines = new String(Files.readAllBytes(theFile.toPath()), "UTF-8").split("\n");

            Market market = new Market(config.get("me"));
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
}
