package org.example.Repository;

import org.example.model.Set;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcSetRepository implements ISetRepository {

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
    public Set save(Set set) {
        String sql = """
            INSERT INTO sets (id, name, release_year)
            VALUES (?, ?, ?)
            ON CONFLICT (id) DO UPDATE
              SET name = EXCLUDED.name,
                  release_year = EXCLUDED.release_year
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, set.getId());
            ps.setString(2, set.getName());

            if (set.getReleaseYear() != null) ps.setInt(3, set.getReleaseYear());
            else ps.setNull(3, Types.INTEGER);

            ps.executeUpdate();
            return set;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving set", e);
        }
    }

    @Override
    public List<Set> findAll() {
        String sql = "SELECT id, name, release_year FROM sets ORDER BY id";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Set> sets = new ArrayList<>();
            while (rs.next()) {
                sets.add(mapRow(rs));
            }
            return sets;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all sets", e);
        }
    }

//    public int countSets() {
//        String sql = "SELECT COUNT(*) FROM sets";
//        try (Connection conn = getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            rs.next();
//            return rs.getInt(1);
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Error counting sets", e);
//        }
//    }

    private Set mapRow(ResultSet rs) throws SQLException {
        Set s = new Set();
        s.setId(rs.getString("id"));
        s.setName(rs.getString("name"));

        int yr = rs.getInt("release_year");
        s.setReleaseYear(rs.wasNull() ? null : yr);

        return s;
    }
}
