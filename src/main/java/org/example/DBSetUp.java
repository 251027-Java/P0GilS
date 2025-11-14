package org.example;

import java.sql.*;
import java.time.*;
import java.util.List;

public final class DBSetUp {
    public static void ensureSchema() throws SQLException {
        String url  = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");

        if (url == null || user == null || pass == null) {
            throw new IllegalStateException("Missing DB envs. Got: DB_URL=" + url + ", DB_USER=" + user + ", DB_PASS=" + (pass != null ? "***" : null));
        }
        try (Connection c = DriverManager.getConnection(url, user, pass)) {
            c.setAutoCommit(false);
            for (String ddl : List.of(
                    "CREATE TABLE IF NOT EXISTS sets (id VARCHAR PRIMARY KEY, name VARCHAR NOT NULL, release_date DATE)",
                    "CREATE TABLE IF NOT EXISTS cards (id VARCHAR PRIMARY KEY, name VARCHAR NOT NULL, set_id VARCHAR REFERENCES sets(id), rarity VARCHAR, small_img VARCHAR, large_img VARCHAR)",
                    "CREATE TABLE IF NOT EXISTS types (id SERIAL PRIMARY KEY, name VARCHAR UNIQUE NOT NULL)",
                    "CREATE TABLE IF NOT EXISTS card_types (card_id VARCHAR REFERENCES cards(id) ON DELETE CASCADE, type_id INT REFERENCES types(id) ON DELETE CASCADE, PRIMARY KEY (card_id, type_id))",
                    "CREATE TABLE IF NOT EXISTS decks (id SERIAL PRIMARY KEY, name VARCHAR UNIQUE NOT NULL)",
                    "CREATE TABLE IF NOT EXISTS deck_cards (deck_id INT REFERENCES decks(id) ON DELETE CASCADE, card_id VARCHAR REFERENCES cards(id) ON DELETE CASCADE, quantity INT NOT NULL CHECK (quantity > 0), PRIMARY KEY (deck_id, card_id))",
                    "CREATE TABLE IF NOT EXISTS price_snapshots (id BIGSERIAL PRIMARY KEY, card_id VARCHAR REFERENCES cards(id) ON DELETE CASCADE, source VARCHAR NOT NULL, market_usd NUMERIC(10,2), trend_eur NUMERIC(10,2), captured_at TIMESTAMP NOT NULL DEFAULT NOW())"
            )) {
                try (PreparedStatement ps = c.prepareStatement(ddl)) {
                    ps.executeUpdate();
                }
            }
            c.commit();
        }
    }
}
