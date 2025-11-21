package org.example.Repository;

import org.example.model.DeckCardView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcDeckCardRepository implements IDeckCardRepository {

    private static final String URL  = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASS = System.getenv("DB_PASS");

    static {
        if (URL == null || USER == null || PASS == null) {
            throw new IllegalStateException("DB_URL, DB_USER, DB_PASS must be set");
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    @Override
    public void addOrUpdate(int deckId, String cardId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }

        String sql = """
            INSERT INTO deck_cards (deck_id, card_id, quantity)
            VALUES (?, ?, ?)
            ON CONFLICT (deck_id, card_id) DO UPDATE
                SET quantity = EXCLUDED.quantity
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, deckId);
            ps.setString(2, cardId);
            ps.setInt(3, quantity);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error adding/updating card in deck", e);
        }
    }

    @Override
    public boolean remove(int deckId, String cardId) {
        String sql = "DELETE FROM deck_cards WHERE deck_id = ? AND card_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, deckId);
            ps.setString(2, cardId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error removing card from deck", e);
        }
    }

    @Override
    public List<DeckCardView> findByDeckId(int deckId) {
        // JOIN
        String sql = """
            SELECT dc.deck_id,
                   dc.card_id,
                   dc.quantity,
                   c.name AS card_name,
                   c.rarity,
                   c.card_type,
                   c.set_id
            FROM deck_cards dc
            JOIN cards c ON c.id = dc.card_id
            WHERE dc.deck_id = ?
            ORDER BY c.name
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, deckId);

            try (ResultSet rs = ps.executeQuery()) {
                List<DeckCardView> items = new ArrayList<>();
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
                return items;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding deck contents", e);
        }
    }

    @Override
    public boolean clearDeck(int deckId) {
        String sql = "DELETE FROM deck_cards WHERE deck_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, deckId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error clearing deck", e);
        }
    }

    private DeckCardView mapRow(ResultSet rs) throws SQLException {
        DeckCardView v = new DeckCardView();
        v.setDeckId(rs.getInt("deck_id"));
        v.setCardId(rs.getString("card_id"));
        v.setQuantity(rs.getInt("quantity"));
        v.setCardName(rs.getString("card_name"));
        v.setRarity(rs.getString("rarity"));
        v.setCardType(rs.getString("card_type"));
        v.setSetId(rs.getString("set_id"));
        return v;
    }
}
