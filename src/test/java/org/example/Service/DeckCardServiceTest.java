package org.example.Service;

import org.example.Repository.ICardRepository;
import org.example.Repository.IDeckCardRepository;
import org.example.Repository.IDeckRepository;
import org.example.model.Card;
import org.example.model.Deck;
import org.example.model.DeckCardView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeckCardServiceTest {

    @Mock
    IDeckCardRepository deckCardRepo;

    @Mock
    IDeckRepository deckRepo;

    @Mock
    ICardRepository cardRepo;

    @InjectMocks
    DeckCardService deckCardService;

    @Test
    void addCardToDeck_happyPath_callsRepo() {
        Deck d = new Deck();
        d.setId(1);
        d.setName("Demo Deck");

        Card c = new Card();
        c.setId("base1-1");
        c.setName("Alakazam");

        when(deckRepo.findById(1)).thenReturn(d);
        when(cardRepo.findById("base1-1")).thenReturn(c);

        deckCardService.addCardToDeck(1, "base1-1", 2);

        verify(deckCardRepo).addOrUpdate(1, "base1-1", 2);
    }

    @Test
    void addCardToDeck_invalidQuantity_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                deckCardService.addCardToDeck(1, "base1-1", 0)
        );

        verify(deckCardRepo, never()).addOrUpdate(anyInt(), anyString(), anyInt());
    }

    @Test
    void addCardToDeck_deckNotFound_throws() {
        when(deckRepo.findById(99)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                deckCardService.addCardToDeck(99, "base1-1", 1)
        );

        verify(deckRepo).findById(99);
        verify(deckCardRepo, never()).addOrUpdate(anyInt(), anyString(), anyInt());
    }

    @Test
    void viewDeckContents_happyPath_returnsJoinRows() {
        Deck d = new Deck();
        d.setId(1);

        when(deckRepo.findById(1)).thenReturn(d);

        DeckCardView v1 = new DeckCardView();
        v1.setDeckId(1);
        v1.setCardId("base1-1");
        v1.setQuantity(2);
        v1.setCardName("Alakazam");

        when(deckCardRepo.findByDeckId(1)).thenReturn(List.of(v1));

        List<DeckCardView> result = deckCardService.viewDeckContents(1);

        assertEquals(1, result.size());
        assertEquals("Alakazam", result.get(0).getCardName());
        verify(deckCardRepo).findByDeckId(1);
    }
}
