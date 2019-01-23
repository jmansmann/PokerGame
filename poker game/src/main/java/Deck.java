
import java.util.*;

/**
 * This class represents a standard deck of 52 playing cards
 */

public class Deck {
  private ArrayList<Card> cards;
  
  public Deck() {
    cards = new ArrayList<Card>();
    for (Suit suit : Suit.values()) {
      for (int rank = 2; rank <= Card.ACE; rank++) { // Ace has highest rank
        cards.add(new Card(rank, suit));
      }
    }
  }
  
  /**
   * Method that pseduorandomly shuffles the deck
   */
  public void shuffle() {
    Collections.shuffle(cards);
  }
  
  /**
   * Method that returns the "top" card of the deck, or null if the deck is empty
   */
  public Card drawCard() {
    return !cards.isEmpty() ? cards.remove(0) : null;
  }
  
}
