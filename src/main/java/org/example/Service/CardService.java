package org.example.Service;

import org.example.Repository.ICardRepository;
import org.example.Repository.ISetRepository;
import org.example.Repository.JdbcSetRepository;
import org.example.model.Card;

import java.util.List;

public class CardService {
    private final ICardRepository cardRepo;
    private final ISetRepository setRepo;

    public CardService(ICardRepository cardRepo, ISetRepository setRepo) {
        this.cardRepo = cardRepo;
        this.setRepo = setRepo;
    }

    public Card getCardById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Card id cannot be empty.");
        }
        Card c = cardRepo.findById(id);
        if (c == null) {
            throw new IllegalArgumentException("No card found with id: " + id);
        }
        return c;
    }

    public List<Card> getCardsBySetId(String setId) {
        if (setId == null || setId.isBlank()) {
            throw new IllegalArgumentException("Set id cannot be empty.");
        }

        boolean exists = setRepo.findAll().stream().anyMatch(s -> setId.equals(s.getId()));
        if (!exists) {
            throw new IllegalArgumentException("Set does not exist: " + setId);
        }

        return cardRepo.findBySetId(setId);
    }

    public Card createCard(Card card) {
        if (card == null) throw new IllegalArgumentException("Card cannot be null.");
        if (card.getId() == null || card.getId().isBlank())
            throw new IllegalArgumentException("Card id cannot be empty.");
        if (card.getName() == null || card.getName().isBlank())
            throw new IllegalArgumentException("Card name cannot be empty.");
        if (card.getSetId() == null || card.getSetId().isBlank())
            throw new IllegalArgumentException("Card must have a setId.");

        boolean setExists = setRepo.findAll().stream().anyMatch(s -> card.getSetId().equals(s.getId()));
        if (!setExists) {
            throw new IllegalArgumentException("Cannot create card. Set does not exist: " + card.getSetId());
        }

        return cardRepo.save(card);
    }
}
