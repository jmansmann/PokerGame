import javax.swing.*;
import javax.swing.border.Border;

import java.awt.Color;
import java.awt.Image;
import java.util.Comparator;
import javax.imageio.ImageIO;

/**
 * This class represents a single card from your standard deck
 * of playing cards
 */
public class Card {
  // Warning: if the values of theses constants change, Deck's initializer may require changes
  public static final int JACK = 11;
  public static final int QUEEN = 12;
  public static final int KING = 13;
  public static final int ACE = 14;

  private final int rank;
  private final Suit suit;

  public Card(int rank, Suit suit) {
    this.rank = rank;
    this.suit = suit;
  }
	
  public int getRank() {
    return rank;
  }
	
  public Suit getSuit() {
    return suit;
  }
  
  /*
   * Custom toString method to return readable string of the card
   */
  public String toString() {
	  String rankStr;
	  if(rank == JACK) {
		  rankStr = "Jack of";
	  }
	  else if (rank == QUEEN) {
		  rankStr = "Queen of";
	  }
	  else if (rank == KING) {
		  rankStr = "King of";
	  }
	  else if (rank == ACE) {
		  rankStr = "Ace of";
	  }
	  else {
		  rankStr = Integer.toString(rank);
	  }
	 
	  String suitStr = suit.toString();
	  return rankStr + " " + suitStr;
  }
  
  public JLabel makeLabel(boolean faceUp) {
    JLabel finalCardPic = new JLabel();
    finalCardPic.setIcon(new ImageIcon(makeImage(faceUp)));
    return finalCardPic;
  }
  
  //makes a new label for the community cards that has a border around it
  public JLabel makeWinningLabel(boolean faceup) {
	  JLabel cardPic = new JLabel();
	  Border border = BorderFactory.createLineBorder(Color.YELLOW, 5);
	  cardPic.setIcon(new ImageIcon(makeImage(faceup)));
	  cardPic.setBorder(border);
	  return cardPic;
  }
  // Need this to get around fact that JLabel's that hold card images
  // are all rendered at the beginning, but cards need to be dealt one
  // at a time
  public Image makeImage(boolean faceUp) {
    Image cardHolder = null;
    if (!faceUp) {
      // Only need picture of back of card (for now)
      try {
        cardHolder = ImageIO.read(getClass().getResource("/back.png"));
        // Re-size the image
        cardHolder = cardHolder.getScaledInstance(75, 112, java.awt.Image.SCALE_SMOOTH);
        
      } catch (Exception e) {}
    } else {
      String fileName = "/" + translateRank(rank) + translateSuit(suit) + ".png";
      try {
        // Read in image
        cardHolder = ImageIO.read(getClass().getResource(fileName));

        // Re-size the image
        cardHolder = cardHolder.getScaledInstance(75, 112, java.awt.Image.SCALE_SMOOTH);
        
      } catch (Exception e) {}
    }
    return cardHolder;
  }

  public static String translateSuit(Suit s) {
    switch (s) {
      case CLUB:
        return "C";
      case DIAMOND:
        return "D";
      case HEART:
        return "H";
      case SPADE:
        return "S";
      default:
        return null;
    }
  }

  public static String translateRank(int rank) {
    switch (rank) {
      case ACE:
        return "A";
      case KING:
        return "K";
      case QUEEN:
        return "Q";
      case JACK:
        return "J";
      default:
        return Integer.toString(rank);
    }
  }

  // Comparator for card sorting, comparisons
  // Default access level in java is package private
  static class CardComparator implements Comparator<Card> {
    // implementing interface
    public int compare(Card x, Card y) {
      return Integer.compare(x.getRank(), y.getRank());
    }
  }
}
