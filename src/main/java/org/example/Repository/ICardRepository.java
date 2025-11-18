package org.example.Repository;

import org.example.model.Card;
import java.util.List;

public interface ICardRepository {
    Card save(Card card);
    Card findById(String id);
    List<Card> findAll();
    List<Card> findBySetId(String setId);
}
