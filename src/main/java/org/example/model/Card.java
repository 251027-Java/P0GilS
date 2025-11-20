package org.example.model;

public class Card {
    // Fields
    private String id;
    private String name;
    private String rarity;
    private String cardType;
    private String setId;

    // Constructors
    public Card() {}

    public Card(String id, String name, String rarity, String cardType, String setId) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.cardType = cardType;
        this.setId = setId;
    }

    // Methods
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getSetId() { return setId; }
    public void setSetId(String setId) { this.setId = setId; }

    @Override
    public String toString() {
        return "Card{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", rarity='" + rarity + '\'' +
                ", cardType='" + cardType + '\'' +
                ", setId='" + setId + '\'' +
                '}';
    }
}
