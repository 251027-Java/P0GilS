package org.example.model;

import java.time.LocalDateTime;

public class Deck {
    // Fields
    private int id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    // Constructor
    public Deck() {}
    public Deck(int id, String name, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Methods
}
