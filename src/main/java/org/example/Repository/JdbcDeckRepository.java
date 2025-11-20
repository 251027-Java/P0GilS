package org.example.Repository;

import org.example.model.Deck;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcDeckRepository implements IDeckRepository {

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
    public Deck save(Deck deck) {
        // If name is unique and you don't want upsert, keep it simple insert.
        // This INSERT returns the generated id + created_at.
        String sql = """
            INSERT INTO decks (name, description)
            VALUES (?, ?)
            RETURNING id, created_at
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, deck.getName());
            ps.setString(2, deck.getDescription());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                deck.setId(rs.getInt("id"));

                // only set createdAt if your model has it
                Timestamp ts = rs.getTimestamp("created_at");
                try {
                    deck.setCreatedAt(ts); // If your Deck uses Timestamp
                    // If your Deck uses LocalDateTime instead:
                    // deck.setCreatedAt(ts.toLocalDateTime());
                } catch (Exception ignored) { }
            }

            return deck;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving deck", e);
        }
    }

    @Override
    public Deck findById(int id) {
        String sql = "SELECT id, name, description, created_at FROM decks WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding deck by id", e);
        }
    }

    @Override
    public Deck findByName(String name) {
        String sql = "SELECT id, name, description, created_at FROM decks WHERE name = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding deck by name", e);
        }
    }

    @Override
    public List<Deck> findAll() {
        String sql = "SELECT id, name, description, created_at FROM decks ORDER BY created_at DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Deck> decks = new ArrayList<>();
            while (rs.next()) decks.add(mapRow(rs));
            return decks;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all decks", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM decks WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting deck", e);
        }
    }

    private Deck mapRow(ResultSet rs) throws SQLException {
        Deck d = new Deck();
        d.setId(rs.getInt("id"));
        d.setName(rs.getString("name"));
        d.setDescription(rs.getString("description"));

        Timestamp ts = rs.getTimestamp("created_at");
        try {
            d.setCreatedAt(ts); // or ts.toLocalDateTime()
        } catch (Exception ignored) { }

        return d;
    }
}
