package org.example;

import org.example.Repository.*;
import org.example.Service.*;
import org.example.model.*;

import java.util.List;
import java.util.Scanner;

public class Main {

    private final SetService setService;
    private final CardService cardService;
    private final DeckService deckService;
    private final DeckCardService deckCardService;

    private final Scanner sc = new Scanner(System.in);

    public Main(SetService setService,
                CardService cardService,
                DeckService deckService,
                DeckCardService deckCardService) {
        this.setService = setService;
        this.cardService = cardService;
        this.deckService = deckService;
        this.deckCardService = deckCardService;
    }

    public static void main(String[] args) {
        try {
            DBSetUp.ensureSchema();
        } catch (Exception e) {
            System.out.println("Failed to create/verify schema.");
            e.printStackTrace();
            return;
        }

        // Repositories
        ISetRepository setRepo = new JdbcSetRepository();
        ICardRepository cardRepo = new JdbcCardRepository();
        IDeckRepository deckRepo = new JdbcDeckRepository();
        IDeckCardRepository deckCardRepo = new JdbcDeckCardRepository();

        // Services
        SetService setService = new SetService(setRepo);
        CardService cardService = new CardService(cardRepo, setRepo);
        DeckService deckService = new DeckService(deckRepo);
        DeckCardService deckCardService =
                new DeckCardService(deckCardRepo, deckRepo, cardRepo);

        new Main(setService, cardService, deckService, deckCardService).run();
    }

    private void run() {
        boolean running = true;
        System.out.println("\n=== Pokémon Deck Builder ===");

        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> showAllSets();
                    case "2" -> showCardsInSet();
                    case "3" -> createDeck();
                    case "4" -> listDecks();
                    case "5" -> addCardToDeck();
                    case "6" -> viewDeckContents();
                    case "7" -> removeCardFromDeck();
                    case "8" -> deleteDeck();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid option. Try again.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Input error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Something went wrong. Try again.");
                e.printStackTrace(); // keep during dev
            }
        }

        System.out.println("Goodbye!");
    }

    private void printMenu() {
        System.out.println("""
                
                -------------------------
                1) View all sets
                2) View cards in a set
                3) Create a deck
                4) List decks
                5) Add card to deck
                6) View deck contents
                7) Remove card from deck
                8) Delete a deck
                0) Exit
                -------------------------
                Enter choice:
                """);
    }

    // Actions
    private void showAllSets() {
        List<Set> sets = setService.getAllSets();
        if (sets.isEmpty()) {
            System.out.println("No sets found. Seed sets first.");
            return;
        }

        System.out.println("\n--- Sets ---");
        sets.stream().limit(50).forEach(s ->
                System.out.println(
                        s.getId() + " | " +
                                s.getName() + " | " +
                                (s.getReleaseYear() == null ? "N/A" : s.getReleaseYear())
                )
        );
        System.out.println("(showing up to 50 sets)\n");
    }

    private void showCardsInSet() {
        System.out.print("Enter set id (ex: base1): ");
        String setId = sc.nextLine().trim();

        List<Card> cards = cardService.getCardsBySetId(setId);
        if (cards.isEmpty()) {
            System.out.println("No cards found for set " + setId);
            return;
        }

        System.out.println("\n--- Cards in set: " + setId + " ---");
        cards.stream().limit(50).forEach(c ->
                System.out.println(
                        c.getId() + " | " +
                                c.getName() + " | " +
                                c.getRarity() + " | " +
                                c.getCardType()
                )
        );
        System.out.println("(showing up to 50 cards)\n");
    }

    private void createDeck() {
        System.out.print("Deck name: ");
        String name = sc.nextLine().trim();

        System.out.print("Description (optional): ");
        String desc = sc.nextLine().trim();
        if (desc.isBlank()) desc = null;

        Deck d = deckService.createDeck(name, desc);
        System.out.println("Created deck: " + d.getId() + " | " + d.getName());
    }

    private void listDecks() {
        List<Deck> decks = deckService.listDecks();
        if (decks.isEmpty()) {
            System.out.println("No decks found.");
            return;
        }

        System.out.println("\n--- Decks ---");
        decks.forEach(d ->
                System.out.println(
                        d.getId() + " | " +
                                d.getName() + " | " +
                                (d.getDescription() == null ? "" : d.getDescription())
                )
        );
        System.out.println();
    }

    private void addCardToDeck() {
        int deckId = promptInt("Enter deck id: ");

        System.out.print("Enter card id (ex: base1-1): ");
        String cardId = sc.nextLine().trim();

        int qty = promptInt("Quantity: ");

        deckCardService.addCardToDeck(deckId, cardId, qty);
        System.out.println("Added/updated " + cardId + " x" + qty + " in deck " + deckId);
    }

    private void viewDeckContents() {
        int deckId = promptInt("Enter deck id: ");

        List<DeckCardView> items = deckCardService.viewDeckContents(deckId);
        if (items.isEmpty()) {
            System.out.println("Deck is empty.");
            return;
        }

        System.out.println("\n--- Deck " + deckId + " Contents ---");
        items.forEach(System.out::println);
        System.out.println();
    }

    private void removeCardFromDeck() {
        int deckId = promptInt("Enter deck id: ");

        System.out.print("Enter card id to remove: ");
        String cardId = sc.nextLine().trim();

        boolean removed = deckCardService.removeCardFromDeck(deckId, cardId);
        System.out.println(removed
                ? "Removed " + cardId + " from deck " + deckId
                : "That card wasn't in the deck.");
    }

    private void deleteDeck() {
        int deckId = promptInt("Enter deck id to delete: ");
        boolean ok = deckService.deleteDeck(deckId);

        System.out.println(ok
                ? "Deleted deck " + deckId
                : "Deck not found.");
    }


    //Helps grab input
    private int promptInt(String msg) {
        while (true) {
            System.out.print(msg);
            String line = sc.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val <= 0) {
                    System.out.println("Please enter a positive number.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }
}
