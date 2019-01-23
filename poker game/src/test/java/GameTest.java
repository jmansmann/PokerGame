import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Game.java
 * Note: This only tests methods that don't deal with GUI
 */

public class GameTest {
  
  /**
   * Unit tests for addToPot()
   * Success Case: Pot amount equals arg
   * Failure Case: Pot amount equals anything else
   */
  
  @Test
  public void addPotTest() {
    Game game = new Game("Test", 2, 30, false);
    game.addToPot(102);
    assertEquals(102, game.getPotAmount());
  }
  
  /**
   * Unit tests for subtractFromPot()
   * Success Case: Pot amount equals 0 - arg
   * Failure Case: Pot amount equals anything else
   */
  
  @Test
  public void subPotTest() {
    Game game = new Game("Test", 2, 30, false);
    game.subtractFromPot(15);
    assertEquals(-15, game.getPotAmount());
  }

  /**
   * Unit tests for checkFlush()
   * Equivalence Classes:
   * Given hand is not a Flush => return null
   * Given hand is a Flush => return winning hand (highest 5 cards only)
   */

  // test Flush is correctly rejected
  @Test
  public void checkFlushInvalidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    Card card1 = new Card(5, Suit.SPADE);
    Card card2 = new Card(6, Suit.SPADE);
    Card card3 = new Card(7, Suit.DIAMOND);
    Card card4 = new Card(10, Suit.HEART);
    Card card5 = new Card(10, Suit.SPADE);
    Card card6 = new Card(Card.QUEEN, Suit.CLUB);
    Card card7 = new Card(Card.ACE, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    assertNull(game.checkFlush(hand));
  }

  // test Flush is detected
  @Test
  public void checkFlushValidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    ArrayList<Card> ans = new ArrayList<Card>();
    Card card1 = new Card(2, Suit.SPADE);
    Card card2 = new Card(4, Suit.SPADE);
    Card card3 = new Card(6, Suit.SPADE);
    Card card4 = new Card(8, Suit.HEART);
    Card card5 = new Card(Card.JACK, Suit.SPADE);
    Card card6 = new Card(Card.QUEEN, Suit.CLUB);
    Card card7 = new Card(Card.QUEEN, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    ans.add(card1);
    ans.add(card2);
    ans.add(card3);
    ans.add(card5);
    ans.add(card7);
    assertTrue(ans.equals(game.checkFlush(hand)));
  }

  // test only top 5 cards are returned when > 5 have same suit
  @Test
  public void checkFlushTopFiveTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    hand.add(new Card(2, Suit.CLUB));
    hand.add(new Card(5, Suit.CLUB));
    hand.add(new Card(8, Suit.CLUB));
    hand.add(new Card(9, Suit.CLUB));
    hand.add(new Card(10, Suit.CLUB));
    hand.add(new Card(Card.JACK, Suit.CLUB));
    hand.add(new Card(Card.QUEEN, Suit.CLUB));
    List<Card> winning = hand.subList(2, 7);
    assertTrue(winning.equals(game.checkFlush(hand)));
  }

  /**
   * Unit tests for checkStraight()
   * Equivalence Classes:
   * Given hand is not a Straight => return null
   * Given hand is a Straight => return winning hand (highest 5 cards only)
   * Given hand is a Straight if Ace is low => return winning hand (highest 5 cards only)
   */

  // test Straight is detected
  @Test
  public void checkStraightValidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    ArrayList<Card> ans = new ArrayList<Card>();
    Card card1 = new Card(2, Suit.SPADE);
    Card card2 = new Card(3, Suit.SPADE);
    Card card3 = new Card(7, Suit.SPADE);
    Card card4 = new Card(8, Suit.HEART);
    Card card5 = new Card(9, Suit.SPADE);
    Card card6 = new Card(10, Suit.CLUB);
    Card card7 = new Card(Card.JACK, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    ans.add(card3);
    ans.add(card4);
    ans.add(card5);
    ans.add(card6);
    ans.add(card7);
    assertTrue(ans.equals(game.checkStraight(hand)));
  }

  // test Straight is correctly rejected
  @Test
  public void checkStraightInvalidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    Card card1 = new Card(5, Suit.SPADE);
    Card card2 = new Card(6, Suit.SPADE);
    Card card3 = new Card(7, Suit.DIAMOND);
    Card card4 = new Card(8, Suit.HEART);
    Card card5 = new Card(10, Suit.SPADE);
    Card card6 = new Card(Card.JACK, Suit.CLUB);
    Card card7 = new Card(Card.QUEEN, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    assertNull(game.checkStraight(hand));
  }

  // test only top 5 cards are returned when > 5 are in Straight
  @Test
  public void checkStraightTopFiveTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    hand.add(new Card(2, Suit.HEART));
    hand.add(new Card(3, Suit.CLUB));
    hand.add(new Card(4, Suit.DIAMOND));
    hand.add(new Card(5, Suit.CLUB));
    hand.add(new Card(6, Suit.SPADE));
    hand.add(new Card(7, Suit.SPADE));
    hand.add(new Card(8, Suit.CLUB));
    List<Card> winning = hand.subList(2, 7);
    assertTrue(winning.equals(game.checkStraight(hand)));
  }

  // test Ace high/low special case
  @Test
  public void checkStraightAce() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    ArrayList<Card> ans = new ArrayList<Card>();
    Card card1 = new Card(2, Suit.CLUB);
    Card card2 = new Card(3, Suit.CLUB);
    Card card3 = new Card(4, Suit.SPADE);
    Card card4 = new Card(5, Suit.HEART);
    Card card5 = new Card(9, Suit.SPADE);
    Card card6 = new Card(Card.JACK, Suit.CLUB);
    Card card7 = new Card(Card.ACE, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    ans.add(card7);
    ans.add(card1);
    ans.add(card2);
    ans.add(card3);
    ans.add(card4);
    assertTrue(ans.equals(game.checkStraight(hand)));
  }

  // test Ace high/low special case when ace doesn't get used
  @Test
  public void checkStraightAce2() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    ArrayList<Card> ans = new ArrayList<Card>();
    Card card1 = new Card(2, Suit.CLUB);
    Card card2 = new Card(3, Suit.CLUB);
    Card card3 = new Card(4, Suit.SPADE);
    Card card4 = new Card(5, Suit.HEART);
    Card card5 = new Card(6, Suit.SPADE);
    Card card6 = new Card(Card.JACK, Suit.CLUB);
    Card card7 = new Card(Card.ACE, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    ans.add(card1);
    ans.add(card2);
    ans.add(card3);
    ans.add(card4);
    ans.add(card5);
    assertTrue(ans.equals(game.checkStraight(hand)));
  }

  /**
   * Unit tests for checkPair()
   * Equivalence Classes:
   * Given hand does not have a Pair => return null
   * Given hand has a Pair => return winning hand, with Pair in highest position
   */

  // test Pair is detected
  @Test
  public void checkPairValidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    ArrayList<Card> ans = new ArrayList<Card>();
    Card card1 = new Card(2, Suit.DIAMOND);
    Card card2 = new Card(3, Suit.SPADE);
    Card card3 = new Card(7, Suit.SPADE);
    Card card4 = new Card(7, Suit.HEART);
    Card card5 = new Card(9, Suit.SPADE);
    Card card6 = new Card(10, Suit.CLUB);
    Card card7 = new Card(Card.JACK, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    ans.add(card1);
    ans.add(card2);
    ans.add(card5);
    ans.add(card6);
    ans.add(card7);
    ans.add(card3);
    ans.add(card4);
    assertTrue(ans.equals(game.checkPair(hand)));
  }

  // test Pair is correctly rejected
  @Test
  public void checkPairInvalidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    Card card1 = new Card(2, Suit.DIAMOND);
    Card card2 = new Card(3, Suit.SPADE);
    Card card3 = new Card(4, Suit.HEART);
    Card card4 = new Card(7, Suit.HEART);
    Card card5 = new Card(9, Suit.SPADE);
    Card card6 = new Card(10, Suit.CLUB);
    Card card7 = new Card(Card.JACK, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    assertNull(game.checkPair(hand));
  }

  /**
   * Unit tests for checkThree()
   * Equivalence Classes:
   * Given hand does not have a Three of a Kind => return null
   * Given hand has one Three of a Kind => return winning three
   * Given hand has two Three of a Kinds => return both threes
   */

  // test Three of a Kind is correctly rejected
  @Test
  public void checkThreeInvalidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    Card card1 = new Card(2, Suit.DIAMOND);
    Card card2 = new Card(3, Suit.SPADE);
    Card card3 = new Card(3, Suit.HEART);
    Card card4 = new Card(7, Suit.HEART);
    Card card5 = new Card(9, Suit.SPADE);
    Card card6 = new Card(Card.JACK, Suit.CLUB);
    Card card7 = new Card(Card.JACK, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    assertNull(game.checkThree(hand));
  }

  // test Three of a Kind is detected
  @Test
  public void checkThreeValidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    ArrayList<Card> ans = new ArrayList<Card>();
    Card card1 = new Card(8, Suit.DIAMOND);
    Card card2 = new Card(9, Suit.SPADE);
    Card card3 = new Card(9, Suit.CLUB);
    Card card4 = new Card(9, Suit.HEART);
    Card card5 = new Card(10, Suit.SPADE);
    Card card6 = new Card(Card.JACK, Suit.CLUB);
    Card card7 = new Card(Card.QUEEN, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    ans.add(card2);
    ans.add(card3);
    ans.add(card4);
    assertTrue(ans.equals(game.checkThree(hand)));
  }

  // test two Three of a Kinds are detected, but only highest returned
  @Test
  public void checkThreeValidTest2() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    ArrayList<Card> ans = new ArrayList<Card>();
    Card card1 = new Card(8, Suit.DIAMOND);
    Card card2 = new Card(8, Suit.SPADE);
    Card card3 = new Card(8, Suit.CLUB);
    Card card4 = new Card(9, Suit.HEART);
    Card card5 = new Card(Card.QUEEN, Suit.SPADE);
    Card card6 = new Card(Card.QUEEN, Suit.CLUB);
    Card card7 = new Card(Card.QUEEN, Suit.HEART);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    ans.add(card5);
    ans.add(card6);
    ans.add(card7);
    assertTrue(ans.equals(game.checkThree(hand)));
  }

  /**
   * Unit tests for checkFour()
   * Equivalence Classes:
   * Given hand does not have a Four of a Kind => return null
   * Given hand has a Four of a Kind => return winning four
   */

  // test Four of a Kind is correctly rejected
  @Test
  public void checkFourInvalidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    Card card1 = new Card(5, Suit.DIAMOND);
    Card card2 = new Card(8, Suit.SPADE);
    Card card3 = new Card(8, Suit.HEART);
    Card card4 = new Card(8, Suit.HEART);
    Card card5 = new Card(9, Suit.SPADE);
    Card card6 = new Card(10, Suit.CLUB);
    Card card7 = new Card(Card.JACK, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    assertNull(game.checkFour(hand));
  }

  // test Four of a Kind is detected
  @Test
  public void checkFourValidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    ArrayList<Card> ans = new ArrayList<Card>();
    Card card1 = new Card(3, Suit.DIAMOND);
    Card card2 = new Card(3, Suit.SPADE);
    Card card3 = new Card(9, Suit.CLUB);
    Card card4 = new Card(9, Suit.HEART);
    Card card5 = new Card(9, Suit.SPADE);
    Card card6 = new Card(9, Suit.DIAMOND);
    Card card7 = new Card(Card.KING, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    ans.add(card3);
    ans.add(card4);
    ans.add(card5);
    ans.add(card6);
    assertTrue(ans.equals(game.checkFour(hand)));
  }

  /**
   * Unit tests for checkTwoPair()
   * Equivalence Classes:
   * Given hand does not have a Two Pair => return null
   * Given hand has two pairs => return two pairs and highest fifth card
   * Given hand has three pairs => return highest two pairs and fifth card
   */

  // test Two Pairs is correctly rejected
  @Test
  public void checkTwoPairInvalidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    Card card1 = new Card(2, Suit.DIAMOND);
    Card card2 = new Card(3, Suit.SPADE);
    Card card3 = new Card(3, Suit.HEART);
    Card card4 = new Card(7, Suit.HEART);
    Card card5 = new Card(9, Suit.SPADE);
    Card card6 = new Card(Card.JACK, Suit.CLUB);
    Card card7 = new Card(Card.KING, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    assertNull(game.checkTwoPair(hand));
  }

  // test Two Pair is detected
  // two pairs and highest fifth card returned
  @Test
  public void checkTwoPairValidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    ArrayList<Card> ans = new ArrayList<Card>();
    Card card1 = new Card(5, Suit.DIAMOND);
    Card card2 = new Card(5, Suit.SPADE);
    Card card3 = new Card(9, Suit.CLUB);
    Card card4 = new Card(10, Suit.HEART);
    Card card5 = new Card(10, Suit.SPADE);
    Card card6 = new Card(Card.JACK, Suit.CLUB);
    Card card7 = new Card(Card.QUEEN, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    ans.add(card7);
    ans.add(card1);
    ans.add(card2);
    ans.add(card4);
    ans.add(card5);
    assertTrue(ans.equals(game.checkTwoPair(hand)));
  }

  // test Two Pair is detected
  // highest two pairs and highest fifth card returned
  @Test
  public void checkTwoPairValidTest2() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    ArrayList<Card> ans = new ArrayList<Card>();
    Card card1 = new Card(5, Suit.DIAMOND);
    Card card2 = new Card(9, Suit.SPADE);
    Card card3 = new Card(9, Suit.CLUB);
    Card card4 = new Card(10, Suit.HEART);
    Card card5 = new Card(10, Suit.SPADE);
    Card card6 = new Card(Card.QUEEN, Suit.CLUB);
    Card card7 = new Card(Card.QUEEN, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    ans.add(card3);
    ans.add(card4);
    ans.add(card5);
    ans.add(card6);
    ans.add(card7);
    assertTrue(ans.equals(game.checkTwoPair(hand)));
  }

  /**
   * Unit tests for checkFullHouse()
   * Equivalence Classes:
   * Given hand does not have a Full House => return null
   * Given hand has Full House => return hand with pair followed by three of a kind
   */

  // test Full House is correctly rejected
  @Test
  public void checkFullHouseInvalidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    Card card1 = new Card(2, Suit.DIAMOND);
    Card card2 = new Card(3, Suit.SPADE);
    Card card3 = new Card(3, Suit.HEART);
    Card card4 = new Card(7, Suit.HEART);
    Card card5 = new Card(9, Suit.SPADE);
    Card card6 = new Card(9, Suit.CLUB);
    Card card7 = new Card(Card.KING, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    assertNull(game.checkFullHouse(hand));
  }

  // test Full House is detected
  // pair followed by three of a kind returned
  @Test
  public void checkFullHouseValidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    ArrayList<Card> ans = new ArrayList<Card>();
    Card card1 = new Card(4, Suit.DIAMOND);
    Card card2 = new Card(5, Suit.SPADE);
    Card card3 = new Card(5, Suit.CLUB);
    Card card4 = new Card(5, Suit.HEART);
    Card card5 = new Card(Card.JACK, Suit.SPADE);
    Card card6 = new Card(Card.JACK, Suit.CLUB);
    Card card7 = new Card(Card.QUEEN, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    ans.add(card5);
    ans.add(card6);
    ans.add(card2);
    ans.add(card3);
    ans.add(card4);
    assertTrue(ans.equals(game.checkFullHouse(hand)));
  }

  /**
   * Unit tests for checkStaightFlush)
   * Equivalence Classes:
   * Given hand does not have a Straight Flush => return null
   * Given hand has Straight Flush => return winning hand
   */

  // test Straight Flush is correctly rejected
  @Test
  public void checkStraightFlushInvalidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    Card card1 = new Card(2, Suit.DIAMOND);
    Card card2 = new Card(3, Suit.SPADE);
    Card card3 = new Card(4, Suit.HEART);
    Card card4 = new Card(5, Suit.HEART);
    Card card5 = new Card(6, Suit.SPADE);
    Card card6 = new Card(7, Suit.CLUB);
    Card card7 = new Card(Card.ACE, Suit.SPADE);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    assertNull(game.checkStraightFlush(hand));
  }

  // test Straight Flush is detected
  @Test
  public void checkStraightFlushValidTest() {
    Game game = new Game("Test", 2, 30, false);
    ArrayList<Card> hand = new ArrayList<Card>();
    ArrayList<Card> ans = new ArrayList<Card>();
    Card card1 = new Card(4, Suit.DIAMOND);
    Card card2 = new Card(5, Suit.SPADE);
    Card card3 = new Card(6, Suit.CLUB);
    Card card4 = new Card(7, Suit.CLUB);
    Card card5 = new Card(8, Suit.CLUB);
    Card card6 = new Card(9, Suit.CLUB);
    Card card7 = new Card(10, Suit.CLUB);
    hand.add(card1);
    hand.add(card2);
    hand.add(card3);
    hand.add(card4);
    hand.add(card5);
    hand.add(card6);
    hand.add(card7);
    ans.add(card3);
    ans.add(card4);
    ans.add(card5);
    ans.add(card6);
    ans.add(card7);
    assertTrue(ans.equals(game.checkStraightFlush(hand)));
  }

}
