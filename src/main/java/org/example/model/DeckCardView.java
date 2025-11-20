package org.example.model;

public class DeckCardView {
    private int deckId;
    private String cardId;
    private String cardName;
    private String rarity;
    private String cardType;
    private String setId;
    private int quantity;

    public DeckCardView() {}

    public int getDeckId() { return deckId; }
    public void setDeckId(int deckId) { this.deckId = deckId; }

    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getSetId() { return setId; }
    public void setSetId(String setId) { this.setId = setId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return cardName + " (" + cardId + ") x" + quantity +
                " | " + rarity + " | " + cardType + " | set=" + setId;
    }
}
