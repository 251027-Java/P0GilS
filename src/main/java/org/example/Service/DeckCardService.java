package org.example.Service;

import org.example.Repository.ICardRepository;
import org.example.Repository.IDeckCardRepository;
import org.example.Repository.IDeckRepository;
import org.example.model.Card;
import org.example.model.Deck;
import org.example.model.DeckCardView;

import java.util.List;

public class DeckCardService {
    private final IDeckCardRepository deckCardRepo;
    private final IDeckRepository deckRepo;
    private final ICardRepository cardRepo;

    public DeckCardService(IDeckCardRepository deckCardRepo,
                           IDeckRepository deckRepo,
                           ICardRepository cardRepo) {
        this.deckCardRepo = deckCardRepo;
        this.deckRepo = deckRepo;
        this.cardRepo = cardRepo;
    }

    public void addCardToDeck(int deckId, String cardId, int quantity) {
        if (deckId <= 0) throw new IllegalArgumentException("Deck id must be positive.");
        if (cardId == null || cardId.isBlank())
            throw new IllegalArgumentException("Card id cannot be empty.");
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be > 0.");

        Deck deck = deckRepo.findById(deckId);
        if (deck == null) {
            throw new IllegalArgumentException("Deck does not exist: id=" + deckId);
        }

        Card card = cardRepo.findById(cardId);
        if (card == null) {
            throw new IllegalArgumentException("Card does not exist: id=" + cardId);
        }

        deckCardRepo.addOrUpdate(deckId, cardId, quantity);
    }

    public boolean removeCardFromDeck(int deckId, String cardId) {
        if (deckId <= 0) throw new IllegalArgumentException("Deck id must be positive.");
        if (cardId == null || cardId.isBlank())
            throw new IllegalArgumentException("Card id cannot be empty.");

        return deckCardRepo.remove(deckId, cardId);
    }

    public List<DeckCardView> viewDeckContents(int deckId) {
        if (deckId <= 0) throw new IllegalArgumentException("Deck id must be positive.");

        Deck deck = deckRepo.findById(deckId);
        if (deck == null) {
            throw new IllegalArgumentException("Deck does not exist: id=" + deckId);
        }

        return deckCardRepo.findByDeckId(deckId);
    }

}
