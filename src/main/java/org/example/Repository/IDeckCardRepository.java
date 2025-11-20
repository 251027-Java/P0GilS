package org.example.Repository;

import org.example.model.DeckCardView;
import java.util.List;

public interface IDeckCardRepository {
    void addOrUpdate(int deckId, String cardId, int quantity);
    boolean remove(int deckId, String cardId);
    List<DeckCardView> findByDeckId(int deckId);
    boolean clearDeck(int deckId); // optional but useful
}
