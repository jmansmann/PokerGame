import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Card.java
 */

public class CardTest {

  /**
   * Unit tests for getSuit()
   * Success Case: given suit returned
   * Failure Case: other value returned or exception thrown
   */

  @Test
  public void getSuitTest() {
    Card card = new Card(7, Suit.CLUB);
    assertEquals(Suit.CLUB, card.getSuit());
  }

  /**
   * Unit tests for getRank()
   * Success Case: given rank returned
   * Failure Case: other value returned or exception thrown
   */

  // test int constants
  @Test
  public void getRankTestConst() {
    Card card = new Card(Card.QUEEN, Suit.HEART);
    assertEquals(12, card.getRank());
  }

  // test number values
  @Test
  public void getRankTestNum() {
    Card card = new Card(6, Suit.HEART);
    assertEquals(6, card.getRank());
  }

}