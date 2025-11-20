package org.example.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Deck {
    // Fields
    private int id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    // Constructors
    public Deck() {}

    public Deck(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Deck(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Deck(int id, String name, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Methods
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setCreatedAt(Timestamp ts) {
        this.createdAt = (ts == null) ? null : ts.toLocalDateTime();
    }

    @Override
    public String toString() {
        return "Deck{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
