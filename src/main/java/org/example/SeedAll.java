package org.example;

import org.example.Repository.JdbcSetRepository;
import org.example.Repository.JdbcCardRepository;
import org.example.api.PokemonTcgApiClient;
import org.example.model.Set;
import org.example.model.Card;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeedAll {

    // Reads the same env vars your repos use
    private static final String URL  = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASS = System.getenv("DB_PASS");

    public static void main(String[] args) {
        try {
            // 0) Make sure all tables exist
            DBSetUp.ensureSchema();

            JdbcSetRepository setRepo = new JdbcSetRepository();
            JdbcCardRepository cardRepo = new JdbcCardRepository();
            PokemonTcgApiClient apiClient = new PokemonTcgApiClient();

            System.out.println("---- DB Connection Info ----");
            setRepo.debugConnection();

            // 1) Seed SETS from sets.json (if present)
            seedSetsFromJson(setRepo, apiClient, "/sets.json");

            // 2) Seed CARDS from one or more cards JSON files
            // Add/remove filenames here as you like:
            String[] cardFiles = {
                    "/cards_base1.json"
                    // "/cards_base2.json",
                    // "/cards_base3.json"
            };
            seedCardsFromJsonFiles(cardRepo, apiClient, cardFiles);

            // 3) Seed DECKS + DECK_CARDS (demo deck)
            seedDemoDeckAndCards();

            System.out.println("✅ Seeding complete.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------
    // 1) Seed Sets
    // -------------------------
    private static void seedSetsFromJson(JdbcSetRepository setRepo,
                                         PokemonTcgApiClient apiClient,
                                         String resourcePath) {
        try {
            String json = loadResource(resourcePath);
            List<Set> sets = apiClient.parseSetsFromJson(json);

            System.out.println("Parsed " + sets.size() + " sets from " + resourcePath);

            for (Set s : sets) {
                setRepo.save(s); // should be safe if save() uses ON CONFLICT
            }

            System.out.println("Sets count after seeding = " + setRepo.countSets());

        } catch (IOException e) {
            System.out.println("No " + resourcePath + " found. Skipping sets seeding.");
        } catch (Exception e) {
            System.out.println("Error seeding sets: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // -------------------------
    // 2) Seed Cards
    // -------------------------
    private static void seedCardsFromJsonFiles(JdbcCardRepository cardRepo,
                                               PokemonTcgApiClient apiClient,
                                               String[] resourcePaths) {
        int totalInserted = 0;

        for (String path : resourcePaths) {
            try {
                String json = loadResource(path);
                List<Card> cards = apiClient.parseCardsFromJson(json);

                System.out.println("Parsed " + cards.size() + " cards from " + path);

                for (Card c : cards) {
                    cardRepo.save(c);   // your save() has ON CONFLICT DO NOTHING
                    totalInserted++;
                }

            } catch (IOException e) {
                System.out.println("No " + path + " found. Skipping this cards file.");
            } catch (Exception e) {
                System.out.println("Error seeding cards from " + path + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Cards count after seeding = " + cardRepo.findAll().size());
    }

    // -------------------------
    // 3) Seed Decks + Deck_Cards
    // -------------------------
    private static void seedDemoDeckAndCards() {
        if (URL == null || USER == null || PASS == null) {
            System.out.println("DB env vars missing; skipping demo deck seeding.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false);

            // 3a) Upsert a deck by name (so reruns don’t crash)
            int deckId;
            String upsertDeck = """
                INSERT INTO decks (name, description)
                VALUES (?, ?)
                ON CONFLICT (name) DO UPDATE
                    SET description = EXCLUDED.description
                RETURNING id
            """;

            try (PreparedStatement ps = conn.prepareStatement(upsertDeck)) {
                ps.setString(1, "Demo Deck");
                ps.setString(2, "Deck seeded by SeedAll");
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    deckId = rs.getInt(1);
                }
            }

            // 3b) Grab 3 existing card ids to add to the deck
            List<String> cardIds = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id FROM cards ORDER BY id LIMIT 3");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cardIds.add(rs.getString(1));
                }
            }

            if (cardIds.isEmpty()) {
                System.out.println("No cards in DB yet. Skipping deck_cards seeding.");
                conn.rollback();
                return;
            }

            // 3c) Insert into deck_cards
            String upsertDeckCards = """
                INSERT INTO deck_cards (deck_id, card_id, quantity)
                VALUES (?, ?, ?)
                ON CONFLICT (deck_id, card_id) DO UPDATE
                    SET quantity = EXCLUDED.quantity
            """;

            try (PreparedStatement ps = conn.prepareStatement(upsertDeckCards)) {
                int qty = 2;
                for (String cardId : cardIds) {
                    ps.setInt(1, deckId);
                    ps.setString(2, cardId);
                    ps.setInt(3, qty);
                    ps.executeUpdate();
                    System.out.println("Added to Demo Deck: " + cardId + " x" + qty);
                    qty = 1; // first card x2, rest x1
                }
            }

            conn.commit();
            System.out.println("Seeded Demo Deck with id=" + deckId);

        } catch (Exception e) {
            System.out.println("Error seeding decks/deck_cards: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // -------------------------
    // Utility: load resource file
    // -------------------------
    private static String loadResource(String path) throws IOException {
        try (InputStream is = SeedAll.class.getResourceAsStream(path)) {
            if (is == null) throw new IOException("Resource not found: " + path);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
