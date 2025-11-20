package org.example.api;

import com.google.gson.*;
import org.example.model.Card;
import org.example.model.Set;

import java.util.ArrayList;
import java.util.List;

public class PokemonTcgApiClient {

    private final Gson gson = new Gson();


    public List<Set> parseSetsFromJson(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray data = root.getAsJsonArray("data");

        List<Set> result = new ArrayList<>();
        for (JsonElement elem : data) {
            JsonObject obj = elem.getAsJsonObject();

            String id = obj.get("id").getAsString();
            String name = obj.get("name").getAsString();

            Integer releaseYear = null;
            if (obj.has("releaseDate") && !obj.get("releaseDate").isJsonNull()) {
                String releaseDate = obj.get("releaseDate").getAsString();
                if (releaseDate.length() >= 4) {
                    releaseYear = Integer.parseInt(releaseDate.substring(0, 4));
                }
            }

            Set s = new Set();
            s.setId(id);
            s.setName(name);
            s.setReleaseYear(releaseYear);
            result.add(s);
        }

        return result;
    }


    public List<Card> parseCardsFromJson(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray data = root.getAsJsonArray("data");

        List<Card> cards = new ArrayList<>();
        for (JsonElement elem : data) {
            JsonObject obj = elem.getAsJsonObject();

            String id = obj.get("id").getAsString();
            String name = obj.get("name").getAsString();

            String rarity = obj.has("rarity") && !obj.get("rarity").isJsonNull()
                    ? obj.get("rarity").getAsString()
                    : null;

            String cardType = obj.has("supertype") && !obj.get("supertype").isJsonNull()
                    ? obj.get("supertype").getAsString()
                    : null;

            String setId = obj.getAsJsonObject("set").get("id").getAsString();

            Card c = new Card(id, name, rarity, cardType, setId);
            cards.add(c);
        }

        return cards;
    }
}
