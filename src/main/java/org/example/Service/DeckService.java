package org.example.Service;

import org.example.Repository.IDeckRepository;
import org.example.model.Deck;

import java.util.List;

public class DeckService {
    private final IDeckRepository deckRepo;

    public DeckService(IDeckRepository deckRepo) {
        this.deckRepo = deckRepo;
    }

    public Deck createDeck(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Deck name cannot be empty.");
        }

        Deck existing = deckRepo.findByName(name);
        if (existing != null) {
            throw new IllegalArgumentException("A deck with that name already exists.");
        }

        Deck deck = new Deck(name.trim(), description == null ? null : description.trim());
        return deckRepo.save(deck);
    }

    public List<Deck> listDecks() {
        return deckRepo.findAll();
    }

    public Deck getDeckById(int id) {
        if (id <= 0) throw new IllegalArgumentException("Deck id must be positive.");
        Deck d = deckRepo.findById(id);
        if (d == null) {
            throw new IllegalArgumentException("No deck found with id: " + id);
        }
        return d;
    }

    public boolean deleteDeck(int id) {
        getDeckById(id);
        return deckRepo.deleteById(id);
    }
}
