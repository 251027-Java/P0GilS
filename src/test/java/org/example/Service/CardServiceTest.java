package org.example.Service;

import org.example.Repository.ICardRepository;
import org.example.Repository.ISetRepository;
import org.example.model.Card;
import org.example.model.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    ICardRepository cardRepo;

    @Mock
    ISetRepository setRepo;

    @InjectMocks
    CardService cardService;

    @Test
    void getCardById_happyPath_returnsCard() {
        Card c = new Card();
        c.setId("base1-1");
        c.setName("Alakazam");

        when(cardRepo.findById("base1-1")).thenReturn(c);

        Card result = cardService.getCardById("base1-1");

        assertEquals("base1-1", result.getId());
        assertEquals("Alakazam", result.getName());
        verify(cardRepo).findById("base1-1");
    }

    @Test
    void getCardById_notFound_throws() {
        when(cardRepo.findById("nope")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                cardService.getCardById("nope")
        );

        verify(cardRepo).findById("nope");
    }

    @Test
    void getCardsBySetId_happyPath_returnsList() {
        Set s = new Set();
        s.setId("base1");
        s.setName("Base Set");

        when(setRepo.findAll()).thenReturn(List.of(s));

        Card c1 = new Card(); c1.setId("base1-1"); c1.setName("Alakazam"); c1.setSetId("base1");
        Card c2 = new Card(); c2.setId("base1-2"); c2.setName("Blastoise"); c2.setSetId("base1");

        when(cardRepo.findBySetId("base1")).thenReturn(List.of(c1, c2));

        List<Card> result = cardService.getCardsBySetId("base1");

        assertEquals(2, result.size());
        verify(setRepo).findAll();
        verify(cardRepo).findBySetId("base1");
    }

    @Test
    void getCardsBySetId_setMissing_throws() {
        when(setRepo.findAll()).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () ->
                cardService.getCardsBySetId("base1")
        );

        verify(setRepo).findAll();
        verify(cardRepo, never()).findBySetId(anyString());
    }
}
