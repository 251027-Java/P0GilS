package org.example.Repository;

import org.example.model.Card;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcCardRepository implements ICardRepository {

    private static final String URL  = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASS = System.getenv("DB_PASS");

    static {
        if (URL == null || USER == null || PASS == null) {
            throw new IllegalStateException(
                    "Missing DB env vars. Make sure DB_URL, DB_USER, DB_PASS are set."
            );
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    @Override
    public Card save(Card card) {
        String sql = """
            INSERT INTO cards (id, name, rarity, card_type, set_id)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, card.getId());
            ps.setString(2, card.getName());
            ps.setString(3, card.getRarity());
            ps.setString(4, card.getCardType());
            ps.setString(5, card.getSetId());

            ps.executeUpdate();
            return card;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving card", e);
        }
    }

    @Override
    public List<Card> findAll() {
        String sql = "SELECT id, name, rarity, card_type, set_id FROM cards ORDER BY id";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Card> cards = new ArrayList<>();
            while (rs.next()) {
                cards.add(mapRow(rs));
            }
            return cards;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all cards", e);
        }
    }

    @Override
    public Card findById(String id) {
        String sql = "SELECT id, name, rarity, card_type, set_id FROM cards WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding card by id", e);
        }
    }

    @Override
    public List<Card> findBySetId(String setId) {
        String sql = "SELECT id, name, rarity, card_type, set_id FROM cards WHERE set_id = ? ORDER BY id";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, setId);

            try (ResultSet rs = ps.executeQuery()) {
                List<Card> cards = new ArrayList<>();
                while (rs.next()) {
                    cards.add(mapRow(rs));
                }
                return cards;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding cards by setId", e);
        }
    }

    private Card mapRow(ResultSet rs) throws SQLException {
        Card c = new Card();
        c.setId(rs.getString("id"));
        c.setName(rs.getString("name"));
        c.setRarity(rs.getString("rarity"));
        c.setCardType(rs.getString("card_type"));
        c.setSetId(rs.getString("set_id"));
        return c;
    }
}
