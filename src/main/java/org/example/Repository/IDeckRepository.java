package org.example.Repository;

import org.example.model.Deck;
import java.util.List;

public interface IDeckRepository {
    Deck save(Deck deck);
    Deck findById(int id);
    Deck findByName(String name);
    List<Deck> findAll();
    boolean deleteById(int id);
}
