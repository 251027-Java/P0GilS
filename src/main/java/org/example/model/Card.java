package org.example.model;

public class Card {
    //Fields
    private String id;
    private String name;
    private String rarity;
    private String cardType;
    private String setId;

    // Constructor
    public Card() {}
    public Card(String id, String name, String rarity, String cardType, String setId) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.cardType = cardType;
        this.setId = setId;
    }

    // Methods
}
