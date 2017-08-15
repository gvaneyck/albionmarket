package com.gvaneyck.albionmarket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
//            List<Recipe> recipes = new ArrayList<>();
//            recipes.add(new Recipe("T3_LEATHER_0_1",
//                    "T3_HIDE_0_1", "2",
//                    "T2_LEATHER_0_1", "1"));

            String recipeContent = new String(Files.readAllBytes(new File("recipes.json").toPath()), "UTF-8");
            List<Recipe> recipes = objectMapper.readValue(recipeContent, new TypeReference<List<Recipe>>() {});

            File[] files = new File("S:\\AlbionMarket").listFiles();
            File theFile = files[files.length - 1];
            String[] lines = new String(Files.readAllBytes(theFile.toPath()), "UTF-8").split("\n");

            Market market = new Market();
            for (String line : lines) {
                try {
                    MarketEntryRaw raw = objectMapper.readValue(line, MarketEntryRaw.class);
                    market.addEntry(raw);
                } catch (Exception e) {
                    // Expected, sometimes the packet reader dumps bad data
                }
            }

//            recipes.get(9).getMaxBuy(market, -0.05);
            for (Recipe recipe : recipes) {
                recipe.getMaxBuy(market, -0.05);
            }

            market.scanBlackMarket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}