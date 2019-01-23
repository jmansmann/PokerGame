import java.lang.Math;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.text.NumberFormat;
import java.io.*;
import javax.imageio.*;
import java.awt.BorderLayout;
import java.awt.Image;
import java.util.*;
import javax.swing.border.BevelBorder;
import javax.swing.*;
import java.lang.Thread;
import javafx.util.Pair;

/**
 * Class representing a Game instance of Texas Holdem
 */
public class Game {

  private final Color SEA_GREEN = new Color(25, 220, 95);
  private final int NUM_ROWS = 3; // Number of rows on the game board
  private final int NUM_COLS = 3; // Number of columns on the game board
  private final int MIDDLE_TILE_INDEX = 1;
  private final int POTENTIAL_SIDE_POTS = 8;
  
  private Player[][] players = new Player[NUM_ROWS][NUM_COLS]; // Players indexed by board position
  private Deck deck = new Deck();
  private int potAmount[] = new int[POTENTIAL_SIDE_POTS];
  private int currentPotIndex = 0;
  private List<Card> communityCards = new ArrayList<Card>();
  private boolean isFirstTurn = true;
  private boolean hasSmallBlindBet = false;
  private boolean hasBigBlindBet = false;
  private int highestBet = 0;
  private int playerTimeLimit = 0;
  private JPanel communityCardsLabel;
  private JFrame frame = new JFrame("Poker Table");
  private JPanel mainPanel = new JPanel();
  private JPanel potLabelHelper;
  private JLabel potLabel = new JLabel("", SwingConstants.CENTER);
  private JLabel[] sidePotLabels = new JLabel[POTENTIAL_SIDE_POTS];
  private JPanel tauntPanel;
  private JLabel tauntLabel;

  public Game(String userName, int numOpponents, int timeLimit, boolean shouldHeckle) {
    if (shouldHeckle) {
      // Display taunt box
      tauntPanel = new JPanel();
      tauntPanel.setPreferredSize(new Dimension(frame.getWidth(), 30));
      tauntPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
      tauntPanel.setLayout(new BoxLayout(tauntPanel, BoxLayout.X_AXIS));
      frame.add(tauntPanel, BorderLayout.NORTH);
      tauntLabel = new JLabel();
      tauntPanel.setVisible(true);
      tauntPanel.add(tauntLabel);
      tauntLabel.setVisible(true);
      tauntLabel.setText("  Expert commentary:");
    }

    deck.shuffle();
    potAmount[currentPotIndex] = 0;
    int numOpponentsToAdd = numOpponents;
    playerTimeLimit = timeLimit;
    RandomName nameGenerator = new RandomName(userName);
    // Add the player, opponents, the pot, and the community cards space
    for (int row = 0; row < NUM_ROWS; row++) {
      for (int col = 0; col < NUM_COLS; col++) {
        if (row == 0 && col == 0) {
          // Add the user
          players[row][col] = new Player(userName, false, this, mainPanel);
        } else if (row == MIDDLE_TILE_INDEX && col == MIDDLE_TILE_INDEX) {
          // Add the pot and community cards
          JPanel middle = new JPanel();
          middle.setLayout(new BoxLayout(middle, BoxLayout.PAGE_AXIS));
          middle.setVisible(true);
          mainPanel.add(middle);
          middle.setBackground(SEA_GREEN);
          String moneyStr = NumberFormat.getCurrencyInstance().format(potAmount[currentPotIndex]);
          potLabel.setText("Pot: " + moneyStr);
          potLabel.setBackground(SEA_GREEN);
          potLabel.setVisible(true);
          // extra panel so community cards aren't cut off
          potLabelHelper = new JPanel();
          potLabelHelper.setBackground(SEA_GREEN);
          potLabelHelper.add(potLabel);
          sidePotLabels[0] = potLabel;  //to keep consistency with potAmount[]; should still be able to just refer to potLabel
          for (int i = 1; i < POTENTIAL_SIDE_POTS; i++) {
            // need 1 through 7 potential side pots
            sidePotLabels[i] = new JLabel("");
            sidePotLabels[i].setBackground(SEA_GREEN);
            sidePotLabels[i].setVisible(false);
            potLabelHelper.add(sidePotLabels[i]);
          }
          middle.add(potLabelHelper);
          communityCardsLabel = new JPanel();
          communityCardsLabel.setBackground(SEA_GREEN);
          communityCardsLabel.setVisible(true);
          middle.add(communityCardsLabel);
        } else if (numOpponentsToAdd > 0) {
          // Add opponents
          String name = nameGenerator.getRandomName();
          players[row][col] = new Player(name, true, this, mainPanel);
          numOpponentsToAdd--;
        } else {
          // Add blank region
          JPanel blankPanel = new JPanel();
          blankPanel.setBackground(SEA_GREEN);
          blankPanel.setVisible(true);
          mainPanel.add(blankPanel);
        }
      }
    }

    mainPanel.setLayout(new GridLayout(NUM_ROWS, NUM_COLS));
    mainPanel.setBackground(SEA_GREEN);
    mainPanel.setVisible(true);
    frame.add(mainPanel);
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
  
  
  /* SIMPLE HELPER METHODS */

  public void displayTaunt(String taunt) {
    tauntLabel.setText(taunt);
  }
  
  public JFrame getFrame() {
	  return frame;
  }
  
  // image that represents when a player has folded their hand
  // also associated with players who are out of money and the game
  public Image makeOutline() {
    Image outlineHolder = null;
    try {
      outlineHolder = ImageIO.read(getClass().getResource("/outline.png"));
      // Re-size the image
      outlineHolder = outlineHolder.getScaledInstance(75, 112, java.awt.Image.SCALE_SMOOTH);
    } catch (Exception e) {}

    return outlineHolder;
  }
  
  public int getPotAmount() {
    return potAmount[currentPotIndex];
  }
  
  public int getPotAmount(int index) {
    return potAmount[index];
  }
  
  public int getHighestBet() {
    return highestBet;
  }

  public void setHighestBet(int highestBet) {
    this.highestBet = highestBet;
  }
  
  public int getCurrentPotIndex() {
    return currentPotIndex;
  }

  public int getPlayerTimeLimit() {
    return playerTimeLimit;
  }
  
  public void decrementPotIndex() {
    currentPotIndex--;
  }
  
  
  /* PLAYER-LOOP HELPER METHODS*/
  
  
  
  public List<Player> getNonFoldedPlayers() {
    List<Player> playerList = new ArrayList<Player>();
    for (Player player : getPlayers()) {
      if (player.getCurrentBet() != Bet.FOLD && player.inGame()) {
        playerList.add(player);
      }
    }
    return playerList;
  }
  
  public int countAllInPlayers() {
    int count = 0;
    for (Player player : getNonFoldedPlayers()) {
      if (player.getAlreadyAllIn()) {
        count += 1;
      }
    }
    return count;
  }
  
  public Player lastPlayerWithMoneyLeft() {
    int count = 0;
    Player last = null;
    for (Player player : getPlayers()) {
      if (player.hasMoney()) {
        count++;
        last = player;
      }
    }
    if (count == 1) {
      return last; // game has ended, only one player left
    }
    else {
      return null; // not ended yet
    }
  }
  
  private boolean allAllIn() {
    for (Player p : getNonFoldedPlayers()) {
      if (!p.getIsAllIn()) {
        return false;
      }
    }
    return true;
  }

  public boolean allButOnePlayerInGameFolded() {
    return playersInGameCount() == foldedPlayersInGameCount() + 1;
  }
  
  private int playersInGameCount() {
    return (int)getPlayers().stream().filter(p -> p.inGame()).count();
  }

  private int foldedPlayersInGameCount() {
    return (int)getPlayers().stream().
        filter(p -> p.inGame() && p.getCurrentBet() == Bet.FOLD).count();
  }
  
  
  
  
  /* PLAYER CREATION AND HANDLING SUBSYSTEM */
  
  /* 
   * Returns a list of players that have enough money to still be in the game, ordered clockwise. 
   * That is, each player in the list is immediately two the left (on the game board) 
   * of the preceding player, where the preceding element of the first player is taken to mean the last player.
   */
  public List<Player> getPlayers() {
    List<Player> playerList = new ArrayList<Player>();
    tryAddPlayer(playerList, 0, 0);
    tryAddPlayer(playerList, 0, 1);
    tryAddPlayer(playerList, 0, 2);
    tryAddPlayer(playerList, 1, 2);
    tryAddPlayer(playerList, 2, 2);
    tryAddPlayer(playerList, 2, 1);
    tryAddPlayer(playerList, 2, 0);
    tryAddPlayer(playerList, 1, 0);
    return playerList;
  }

  private void tryAddPlayer(List<Player> playerList, int row, int col) {
    if (players[row][col] != null) {
      playerList.add(players[row][col]);
    }
  }
  
  
  /* BETTING SUBSYSTEM */
  
  public void dealToPlayers() {
    for (Player player : getPlayers()) {
      if (player.inGame()) {
        player.setCard1(deck.drawCard());
        player.updateDisplay();
      }
    }

    for (Player player : getPlayers()) {
      if (player.inGame()) {
        player.setCard2(deck.drawCard());
        player.updateDisplay();
      }
    }

    frame.repaint();
    frame.revalidate();
    frame.setVisible(true);
  }
  
  public void addToPot(int amount) {
    subtractFromPot(-amount);
  }

  public void subtractFromPot(int amount) {
    subtractFromPot(amount, currentPotIndex);
  }
  
  public void subtractFromPot(int amount, int potIndex) {
    potAmount[potIndex] -= amount;
    String moneyStr = NumberFormat.getCurrencyInstance().format(potAmount[potIndex]);
    if (potIndex == 0) {
      potLabel.setText("Pot: " + moneyStr);
    } else {
      sidePotLabels[potIndex].setText("Side-Pot " + potIndex + ": " + moneyStr);
      sidePotLabels[potIndex].setVisible(true);
    }
    frame.repaint();
    frame.revalidate();
    frame.setVisible(true);
  }

  public void bet(Logger logger) {
    resetIsEqualized();
    clearBetThisRound();
    // need to keep track if any money has been bet this round for "AI"
    boolean moneyBet = false;
    boolean createNewPot = false;
    int i = nextPlayerIndex(dealerIndex());
    while (!(allEqualized() || allButOnePlayerInGameFolded())) {
      Player player = getPlayers().get(i);
      player.checkCallButtonText();
      if (player.getCurrentBet() != Bet.FOLD && player.hasMoney() && !player.getIsAllIn()) {
        player.bet(moneyBet, logger);
        if (player.getCurrentBet() == Bet.RAISE) {
          moneyBet = true;
        }
        player.updateDisplay();
        if (player.getIsAllIn() && !player.getAlreadyAllIn()) {
          // checks if a player has just gone all-in during this round,
          // and if they have, will call handleSidePots() below
          createNewPot = true;
        }
      }
      i = nextPlayerIndex(i);
    }
    if (createNewPot && !allAllIn()) {
      handleSidePots();
    }
  }
  
  
  private int getMoneyBetThisRound() {
    int sum = 0;
    for (Player p : getNonFoldedPlayers()) {
      sum += p.getBetThisRound();
    }
    return sum;
  }
  
  /*
   * Gets called when one or more players goes all-in
   * during the current betting round. From there, it sorts players
   * by their betThisRound, and for every player who has a bet
   * lower than the highest bet of the round, makes a new side pot.
   * Players can only win pots that they have put money into
   */
  private void handleSidePots() {
    List<Player> players = getNonFoldedPlayers();
    Collections.sort(players);
    int currentSize = players.size() - countAllInPlayers();
    int lowestTotalBet = 0;
    int highestTotalBet = 0;
    for (Player p : players) {
      highestTotalBet = p.getBetThisRound();
    } // sorted so highest value is at end
    int moneyBetThisRound = getMoneyBetThisRound();
    boolean firstIteration = true;
    
    for (Player player : players) {
      int currentBet = player.getBetThisRound();
      if (currentBet > lowestTotalBet && currentBet <= highestTotalBet) {
        if (firstIteration && getPotAmount() >= moneyBetThisRound) {
          subtractFromPot(moneyBetThisRound);
          firstIteration = false;
        }
        addToPot((currentBet - lowestTotalBet) * currentSize);
        lowestTotalBet = currentBet;
        currentPotIndex++;
        
        for (Player p : getNonFoldedPlayers()) {
          if (p.getBetThisRound() == lowestTotalBet && p.getIsAllIn()) {
            p.setAlreadyAllIn(true);
            currentSize--;
          } else if (p.getBetThisRound() >= lowestTotalBet && !p.getAlreadyAllIn()) {
            p.setHasBetThisPot(currentPotIndex, true);
          }
        }
      }
    }
    
    if (currentSize == 1) {
      for (Player player : getNonFoldedPlayers()) {
        if (player.checkHasBetThisPot(currentPotIndex)) {
          // only 1 player still in game that's not all-in,
          // and they put too much money into newest pot; return money
          subtractFromPot(getPotAmount());
          player.refundBet(getPotAmount());
        }
      }
    } else if (getPotAmount() == 0) {
      // made an extra side-pot; undo
      sidePotLabels[currentPotIndex].setVisible(false);
      decrementPotIndex();
    }
  }
  
  
  /* HAND RESET SUBSYSTEM */
  
  public void reset() {
    deck = new Deck();
    deck.shuffle();
    communityCards.clear();
    displayCommunityCards();
    hasSmallBlindBet = false;
    hasBigBlindBet = false;
    highestBet = 0;
    currentPotIndex = 0;
    isFirstTurn = false;
    for (Player player : getPlayers()) {
      player.resetBetting();
      for (int i = 1; i < POTENTIAL_SIDE_POTS; i++) {
        // 8 is length of hasBetThisPot array; start at 1 b/c index 0
        // represents the main pot, which is always true for all players
        player.setHasBetThisPot(i, false);
        sidePotLabels[i].setVisible(false);
      }
    }
    if (lastPlayerWithMoneyLeft() != null) {
      for (Player player : getPlayers()) {
        player.resetMoney();
      }
    }
  }
  
  private boolean allEqualized() {
    for (Player player : getPlayers()) {
      if (player.inGame() && !player.getIsEqualized()) {
        return false;
      }
    }
    return true;
  }
  
  private void clearBetThisRound() {
    for (Player player : getNonFoldedPlayers()) {
      player.resetBetThisRound();
    }
  }
  
  private void resetIsEqualized() {
    for (Player player : getNonFoldedPlayers()) {
      if (player.getCurrentBet() != Bet.ALL_IN) {
        player.setIsEqualized(!player.inGame());
      }
    }
  }
  
  private void resetPlayersBetThisRound() {
	  for (Player player : getNonFoldedPlayers()) {
		  player.setHasBetThisRound(false);
	  }
  }
  
  
  /* DEALER AND BLINDS HANDLING SUBSYSTEM */
  
  private int dealerIndex() {
    for (int i = 0; i < getPlayers().size(); i++) {
      if (getPlayers().get(i).getIsDealer()) {
        return i;
      }
    }
    return -1;
  }

  public Player getDealer() {
    return getPlayers().get(dealerIndex());
  }
  
  public void assignDealerAndBlinds() {
    Player dealer;
    if (isFirstTurn) {
      dealer = getPlayers().get((new Random()).nextInt(getPlayers().size()));
    } else {
      int oldDealerIndex = dealerIndex();
      getPlayers().get(oldDealerIndex).setIsDealer(false);
      dealer = getPlayers().get(nextPlayerInGameIndex(oldDealerIndex));
    }
    dealer.setIsDealer(true);

    for (Player player : getPlayers()) {
      player.updateDisplay();
    }
  }

  private int nextPlayerIndex(int currentPlayerIndex) {
    return currentPlayerIndex + 1 < getPlayers().size() ? currentPlayerIndex + 1 : 0;
  }
  
  private int nextPlayerInGameIndex(int playerIndex) {
    int i = nextPlayerIndex(playerIndex);
    while (true) {
      if (getPlayers().get(i).inGame()) {
        return i;
      }
      i = nextPlayerIndex(i);
    }
  }

  public Player getLittleBlind() {
    return getPlayers().get(nextPlayerInGameIndex(dealerIndex()));
  }

  public Player getBigBlind() {
    return getPlayers().get(nextPlayerInGameIndex(nextPlayerInGameIndex(dealerIndex())));
  }
  
  public boolean getHasBigBlindBet() {
    return hasBigBlindBet;
  }

  public void setHasBigBlindBet(boolean hasBet) {
    hasBigBlindBet = hasBet;
  }

  public boolean getHasSmallBlindBet() {
    return hasSmallBlindBet;
  }

  public void setHasSmallBlindBet(boolean hasBet) {
    hasSmallBlindBet = hasBet;
  }
  
  public void dealCard() {
    resetPlayersBetThisRound(); //every time a new card is dealt to community cards, reset betting for this round
    communityCards.add(deck.drawCard());
    displayCommunityCards();
  }
  
  private void displayCommunityCards() {
    communityCardsLabel.removeAll();
    for (Card card : communityCards) {
      communityCardsLabel.add(card.makeLabel(true));
      communityCardsLabel.setVisible(true);
    }
    communityCardsLabel.repaint();
    communityCardsLabel.revalidate();
    frame.repaint();
    frame.revalidate();
  }
  
  public List<Card> getCommunityCards() {
	  return communityCards;
  }
  
  public void flipPlayerCards() {
    getNonFoldedPlayers().forEach((player) -> player.flipCards());
  }
  
  
  /* HAND WINNERS CALCULATION SUBSYSTEM */
  
  public Pair<HashMap<Player, List<Card>>, String> doShowdown() {
    for (Player player : getNonFoldedPlayers()) {
      if (player.inGame()) {
        player.flipCards();
      }
    }
    
    Pair<HashMap<Player, List<Card>>, String> winners = getWinners(getPlayers(), communityCards);
    String message = getWinnerMessage(winners, currentPotIndex);
    JOptionPane.showMessageDialog(null, message);
    
    return winners;
  }

  //displays a border around cards on the table that contributed to the winning hand(s)
  public void displayWinningCards(Pair<HashMap<Player, List<Card>>, String> winners, int potIndex) {
	  for (Player player : getPlayers()) {
		  player.removeCardBorder();
	  }
	  List<Card> playerWinningCards = new ArrayList<Card>();
	  Player winner = winners.getKey().entrySet().iterator().next().getKey();
	  String winningHandType = winners.getValue();
      List<Card> winningCards = winners.getKey().entrySet().iterator().next().getValue();
      
      //checks if the winning hand is of type "One Pair" or "High Card"
      //and makes it so that only the two cards in the pair (or high card)
      //are highlighted on table
      if(winningHandType.equals("One Pair")) {
    	  Card winningPairCard1 = winningCards.get(winningCards.size() - 1);
    	  Card winningPairCard2 = winningCards.get(winningCards.size() - 2);
    	  winningCards = new ArrayList<Card>();
    	  winningCards.add(winningPairCard1);
    	  winningCards.add(winningPairCard2);
      }
      else if (winningHandType.equals("High Card")) {
    	  Card highCard = winningCards.get(winningCards.size() - 1);
    	  winningCards = new ArrayList<Card>();
    	  winningCards.add(highCard);
      }
	  for (Card card : winningCards) {
		 if (card == winner.getCard1() || card == winner.getCard2()) {
			 playerWinningCards.add(card);
		 }
	  }
	  winner.displayWinningCards(playerWinningCards);
	  displayWinningCommunityCards(winningCards);
  }
  
  //repaints the community cards to have a yellow border around the 
  //cards that contributed to the winning hand
  private void displayWinningCommunityCards(List<Card> winningCards) {
	  communityCardsLabel.removeAll();
	  for (Card card : communityCards) {
		  if(winningCards.contains(card)) {
		      communityCardsLabel.add(card.makeWinningLabel(true));
		      communityCardsLabel.setVisible(true);
		  }
		  else {
		      communityCardsLabel.add(card.makeLabel(true));
		      communityCardsLabel.setVisible(true);
		  }
	  }
	  communityCardsLabel.repaint();
	  communityCardsLabel.revalidate();
  }
  
  // show winner message
  public String getWinnerMessage(Pair<HashMap<Player, List<Card>>, String> winners, int potIndex) {
    String message = "";
    if (winners.getKey().size() == 1) {
      // only one winner
      Player winner = winners.getKey().entrySet().iterator().next().getKey();
      List<Card> winnerCards = winners.getKey().entrySet().iterator().next().getValue();
      //displayWinningCards(winnerCards, winner);
      if (potIndex > 0) {
        message += winner.getName() + " wins side pot #" + potIndex + " with ";
      } else {
        message += winner.getName() + " wins the main pot with ";
      }
      
      message += winners.getValue() + " (";
      String winningHandType = winners.getValue();
      
      //checks if the winning hand is of type "One Pair" or "High Card"
      //and makes it so that only the two cards in the pair (or high card)
      //are written to screen
      if(winningHandType.equals("One Pair")) {
    	  Card winningPairCard1 = winnerCards.get(winnerCards.size() - 1);
    	  Card winningPairCard2 = winnerCards.get(winnerCards.size() - 2);
    	  winnerCards = new ArrayList<Card>();
    	  winnerCards.add(winningPairCard1);
    	  winnerCards.add(winningPairCard2);
      }
      else if (winningHandType.equals("High Card")) {
    	  Card highCard = winnerCards.get(winnerCards.size() - 1);
    	  winnerCards = new ArrayList<Card>();
    	  winnerCards.add(highCard);
      }
      for (int i = 0; i < winnerCards.size(); i++) {
        Card card = winnerCards.get(i);
        message += Card.translateRank(card.getRank()) + Card.translateSuit(card.getSuit());
        if (i == (winnerCards.size() - 1)) {
          // last card
          message += ")";
        }
        else {
          message += ", ";
        }
      }
    }
    else {
      // multiple winners
      if (potIndex > 0) {
        message += "Winners of side pot #" + potIndex + ":\n";
      } else {
        message += "Winners of the main pot:\n";
      }
      
      for (Map.Entry<Player, List<Card>> entry : winners.getKey().entrySet()) {
        Player winner = entry.getKey();
        List<Card> winnerCards = entry.getValue();
        message += winner.getName() + " with ";
        message += winners.getValue() + " (";
        for (int i = 0; i < winnerCards.size(); i++) {
          Card card = winnerCards.get(i);
          message += Card.translateRank(card.getRank()) + Card.translateSuit(card.getSuit());
          if (i == (winnerCards.size() - 1)) {
            // last card
            message += ")";
          }
          else {
            message += ", ";
          }
        }
        message += "\n";
      }
    }
    return message;
  }

  // Get the winner(s) of the round based on ranks of poker hands
  public Pair<HashMap<Player, List<Card>>, String> getWinners(List<Player> players, List<Card> community) {
    // storage for each player's hand
    // indexed by player for easy use in for/each loops
    // and to associate hands with players
    HashMap<Player, ArrayList<Card>> playerHands = new HashMap<Player, ArrayList<Card>>();
    // get each players collective hand
    for (Player player : players) {
      // check if they folded
      if (player.getCurrentBet() != Bet.FOLD) {
        // create new hand from player cards + community cards
        ArrayList<Card> hand = new ArrayList<Card>(community);
        hand.add(player.getCard1());
        hand.add(player.getCard2());
        // sort
        Collections.sort(hand, new Card.CardComparator());
        // add to hashmap
        playerHands.put(player, hand);
      }
    }

    // rank each player's hand
    int highestRank = 0;
    HashMap<Player, Integer> playerRanks = new HashMap<Player, Integer>();
    HashMap<Player, List<Card>> winningHands = new HashMap<Player, List<Card>>();
    for (Map.Entry<Player, ArrayList<Card>> entry : playerHands.entrySet()) {
      // check each possible hand
      // there are some magic numbers here but they're pretty easy to understand
      List<Card> newHand;
      // Straight Flush: 8
      newHand = checkStraightFlush(entry.getValue());
      if (newHand != null) {
        // record rank for this player
        playerRanks.put(entry.getKey(), 8);
        // store winning hand
        winningHands.put(entry.getKey(), newHand);
        if (highestRank < 8) { highestRank = 8; }
        // rank next player
        continue;
      }

      // Four of a Kind: 7
      newHand = checkFour(entry.getValue());
      if (newHand != null) {
        // record rank for this player
        playerRanks.put(entry.getKey(), 7);
        // store winning hand
        winningHands.put(entry.getKey(), newHand);
        if (highestRank < 7) { highestRank = 7; }
        // rank next player
        continue;
      }

      // Full House: 6
      newHand = checkFullHouse(entry.getValue());
      if (newHand != null) {
        // record rank for this player
        playerRanks.put(entry.getKey(), 6);
        // store winning hand
        winningHands.put(entry.getKey(), newHand);
        if (highestRank < 6) { highestRank = 6; }
        // rank next player
        continue;
      }

      // Flush: 5
      newHand = checkFlush(entry.getValue());
      if (newHand != null) {
        // record rank for this player
        playerRanks.put(entry.getKey(), 5);
        // store winning hand
        winningHands.put(entry.getKey(), newHand);
        if (highestRank < 5) { highestRank = 5; }
        // rank next player
        continue;
      }

      // Straight: 4
      newHand = checkStraight(entry.getValue());
      if (newHand != null) {
        // record rank for this player
        playerRanks.put(entry.getKey(), 4);
        // store winning hand
        winningHands.put(entry.getKey(), newHand);
        if (highestRank < 4) { highestRank = 4; }
        // rank next player
        continue;
      }

      // Three of a Kind: 3
      newHand = checkThree(entry.getValue());
      if (newHand != null) {
        // record rank for this player
        playerRanks.put(entry.getKey(), 3);
        // store winning hand
        winningHands.put(entry.getKey(), newHand);
        if (highestRank < 3) { highestRank = 3; }
        // rank next player
        continue;
      }

      // Two Pair: 2
      newHand = checkTwoPair(entry.getValue());
      if (newHand != null) {
        // record rank for this player
        playerRanks.put(entry.getKey(), 2);
        // store winning hand
        winningHands.put(entry.getKey(), newHand);
        if (highestRank < 2) { highestRank = 2; }
        // rank next player
        continue;
      }

      // One Pair: 1
      newHand = checkPair(entry.getValue());
      if (newHand != null) {
        // record rank for this player
        playerRanks.put(entry.getKey(), 1);
        // store winning hand
        winningHands.put(entry.getKey(), newHand);
        if (highestRank < 1) { highestRank = 1; }
        // rank next player
        continue;
      }

      // High Card: 0
      // record rank for this player
      playerRanks.put(entry.getKey(), 0);
      // store winning hand
      winningHands.put(entry.getKey(), entry.getValue());

    }

    // pick winners
    HashMap<Player, List<Card>> winners = new HashMap<Player, List<Card>>();
    for (Map.Entry<Player, Integer> entry : playerRanks.entrySet()) {
      if (entry.getValue() == highestRank) {
        // add to list of potential winners
        winners.put(entry.getKey(), winningHands.get(entry.getKey()));
      }
    }
    // if winners == 1, return
    // otherwise run tie breaker routine
    if (winners.size() == 1) {
      // return the winner, hand
      String handType = translateHand(highestRank);
      Pair<HashMap<Player, List<Card>>, String> finalWinners = new Pair<>(winners, handType);
      return finalWinners;
    }
    else if (winners.size() > 1) {
      // more than one potential winner, check high values for tie breaker TODO
      // note that this can still result in a tie if high values are the same
      HashMap<Player, List<Card>> lead = new HashMap<Player, List<Card>>();
      int highValue = 0;
      for (Map.Entry<Player, List<Card>> entry : winners.entrySet()) {
        List<Card> cards = entry.getValue();
        int i = cards.size() - 1; // move backwards highest -> lowest
        if (cards.get(i) != null && cards.get(i).getRank() > highValue) {
          // new highest value
          highValue = cards.get(i).getRank();
          // remove previous leaders
          lead = new HashMap<Player, List<Card>>();
          // add first new
          lead.put(entry.getKey(), entry.getValue());
        }
        else if (cards.get(i) != null && cards.get(i).getRank() == highValue) {
          // check next highest cards in order until higher found or out of cards (tie)
          // loop backwards comparing cards with a leader (all leaders must be tied value wise)
          List<Card> leadCards = lead.entrySet().iterator().next().getValue();
          i--;
          while(cards.get(i) != null && leadCards.get(i) != null) {
            if (cards.get(i).getRank() > leadCards.get(i).getRank()) {
              // new leader
              // don't update highValue because still the same with higher lower card
              lead = new HashMap<Player, List<Card>>();
              lead.put(entry.getKey(), entry.getValue());
              break;
            }
            else if (cards.get(i).getRank() < leadCards.get(i).getRank()) {
              // lower value than leader, quit
              break;
            }
            // else still tied
            // check if last card
            if (i == 0) {
              // true tie, add as another leader
              lead.put(entry.getKey(), entry.getValue());
              break;
            }
            i--;
          }
        }
        // else player's high value is less than highest, ignore
      }
      // return the winners, hand
      String handType = translateHand(highestRank);
      Pair<HashMap<Player, List<Card>>, String> finalWinners = new Pair<>(lead, handType);
      return finalWinners;
    }

    return null; // error
  }

  // Check if given hand is a Flush
  // Returns:
  // null if not a Flush
  // Winning hand if a Flush (highest 5 cards only)
  public List<Card> checkFlush(List<Card> hand) {
    // collect cards into suits
    ArrayList<Card> c = new ArrayList<Card>();
    ArrayList<Card> d = new ArrayList<Card>();
    ArrayList<Card> h = new ArrayList<Card>();
    ArrayList<Card> s = new ArrayList<Card>();
    for(Card card : hand) {
      String suit = Card.translateSuit(card.getSuit());
      if (suit.equals("C")) { c.add(card); }
      else if (suit.equals("D")) { d.add(card); }
      else if (suit.equals("H")) { h.add(card); }
      else if (suit.equals("S")) { s.add(card); }
    }
    // check if any suit has more than 5 cards
    if (!(c.size() >= 5 || d.size() >= 5 || h.size() >= 5 || s.size() >= 5)){ return null; }
    // get the suit with more than 5
    ArrayList<Card> winning = null;
    if (c.size() >= 5) {
      winning = c;
    }
    else if (d.size() >= 5) {
      winning = d;
    }
    else if (h.size() >= 5) {
      winning = h;
    }
    else if (s.size() >= 5) {
      winning = s;
    }
    // only take the 5 highest value cards (hand previously sorted)
    return winning.subList(winning.size() - 5, winning.size());
  }

  // Check if given hand is a Straight
  // Returns:
  // null if not a Straight
  // Winning hand if a Straight
  public List<Card> checkStraight(List<Card> hand) {
    // check for sequence of 5 ranks
    ArrayList<Card> winning = null;
    for (int i = 0; i < hand.size(); i++) {
      // loop next cards looking for sequence
      // add to list if correct sequence
      int rank = hand.get(i).getRank();
      ArrayList<Card> temp = new ArrayList<Card>();
      int j = 0;
      while(i + j < hand.size() && hand.get(i + j).getRank() == rank + j) {
        temp.add(hand.get(i + j));
        // check ace high/low special case
        if (temp.size() == 4 && hand.get(i + j).getRank() == 5 && hand.get(hand.size() - 1).getRank() == Card.ACE) {
          // make it the lowest card
          temp.add(0, hand.get(hand.size() - 1));
        }
        j++;
      }
      // if list is long enough to be a straight
      if (temp.size() >= 5) {
        winning = temp;
        break;
      }
    }
    if (winning == null) { return null; }
    // only take the 5 highest value cards (hand previously sorted)
    return winning.subList(winning.size() - 5, winning.size());
  }

  // Check if given hand has a single Pair
  // Returns:
  // null if no Pair
  // Winning hand if a Pair exists
  public List<Card> checkPair(List<Card> hand) {
    ArrayList<Card> winning = null;
    for (int i = 0; i < hand.size(); i++) {
      int rank = hand.get(i).getRank();
      if (i + 1 < hand.size() && hand.get(i + 1).getRank() == rank) {
        // pair found
        Card card1 = hand.get(i);
        Card card2 = hand.get(i + 1);
        winning = new ArrayList<Card>(hand);
        // remove the cards in the pair
        winning.remove(card1);
        winning.remove(card2);
        // re-add them at the end of the list ("highest value" over unmatched cards)
        winning.add(card1);
        winning.add(card2);
        break; // stop searching
      }
    }
    return winning;
  }

  // Check if given hand has a Three of a Kind
  // Returns:
  // null if no Three of a Kind
  // Winning hand if contains at least one Three of a Kind
  // return only highest Three of a Kind if two exist
  public List<Card> checkThree(List<Card> hand) {
    ArrayList<Card> winning = new ArrayList<Card>();
    for (int i = 0; i < hand.size(); i++) {
      int rank = hand.get(i).getRank();
      if (i + 2 < hand.size() && hand.get(i + 1).getRank() == rank && hand.get(i + 2).getRank() == rank) {
        // three of a kind found
        winning.add(hand.get(i));
        winning.add(hand.get(i + 1));
        winning.add(hand.get(i + 2));
      }
    }
    if (winning.isEmpty()) { return null; }
    // return only highest 3
    return winning.subList(winning.size() - 3, winning.size());
  }

  // Check if given hand has a Four of a Kind
  // Returns:
  // null if no Four of a Kind
  // Winning hand if contains at least one Four of a Kind
  public List<Card> checkFour(List<Card> hand) {
    ArrayList<Card> winning = new ArrayList<Card>();
    for (int i = 0; i < hand.size(); i++) {
      int rank = hand.get(i).getRank();
      if (i + 3 < hand.size() && hand.get(i + 1).getRank() == rank && hand.get(i + 2).getRank() == rank && hand.get(i + 3).getRank() == rank) {
        // four of a kind found
        winning.add(hand.get(i));
        winning.add(hand.get(i + 1));
        winning.add(hand.get(i + 2));
        winning.add(hand.get(i + 3));
        break;  // only one possible
      }
    }
    if (winning.isEmpty()) { return null; }
    return winning;
  }

  // Check if given hand has Two Pair
  // Returns:
  // null if hand doesn't have Two Pair
  // Winning hand of highest two pairs and highest fifth card if has Two Pair
  public List<Card> checkTwoPair(List<Card> hand) {
    ArrayList<Card> winning = new ArrayList<Card>();
    ArrayList<Card> temp = new ArrayList<Card>();
    for (int i = 0; i < hand.size(); i++) {
      int rank = hand.get(i).getRank();
      if (i + 1 < hand.size() && hand.get(i + 1).getRank() == rank) {
        // pair found
        Card card1 = hand.get(i);
        Card card2 = hand.get(i + 1);
        // store pair
        temp.add(card1);
        temp.add(card2);
      }
    }
    // check if no pairs or not enough pairs
    if (temp.isEmpty() || temp.size() < 4) { return null; }
    // only want highest 2 pairs (3 possible)
    List<Card> highestPairs = temp.subList(temp.size() - 4, temp.size());
    // add pairs to winning hand and remove from consideration to find 5th card
    ArrayList<Card> fifth = new ArrayList<Card>(hand);
    for (Card card : highestPairs) {
      winning.add(card);
      fifth.remove(card);
    }
    // get highest card remaining
    Card fifthCard = fifth.get(fifth.size() - 1);
    // add to front (checked last for tie break)
    winning.add(0, fifthCard);
    return winning;
  }

  // Check if given hand has a Full House
  // Returns:
  // null if hand doesn't have a Full House
  // Winning hand of Pair followed by Three of a Kind if Full House
  public List<Card> checkFullHouse(List<Card> hand) {
    ArrayList<Card> winning = new ArrayList<Card>();
    List<Card> check = new ArrayList<Card>(hand);
    List<Card> temp;
    // first check if there is a three of a kind
    temp = checkThree(check);
    if (temp != null) {
      // add to winning, remove from consideration
      winning.addAll(temp);
      check.removeAll(temp);
      // check if there is a pair
      temp = checkPair(check);
      if (temp != null) {
        // add pair to front of winning hand (checked last during tie break)
        winning.addAll(0, temp);
        // only return pair + three of a kind, trim unmatched cards from pair check
        return winning.subList(winning.size() - 5, winning.size());
      }
    }
    return null;
  }

  // Check if given hand has a Straight Flush
  // Returns:
  // null if hand doesn't have a Straight Flush
  // Winning hand if Straight Flush
  public List<Card> checkStraightFlush(List<Card> hand) {
    ArrayList<Card> winning = new ArrayList<Card>();
    List<Card> temp;
    // first check if there is a flush
    temp = checkFlush(hand);
    if (temp != null) {
      // add flush to winning
      winning.addAll(temp);
      // check if flush is also a straight
      temp = checkStraight(winning);
      if (temp != null) {
        return winning;
      }
    }
    return null;
  }

  private String translateHand(int rank) {
    switch (rank) {
      case 0:
        return "High Card";
      case 1:
        return "One Pair";
      case 2:
        return "Two Pair";
      case 3:
        return "Three of a Kind";
      case 4:
        return "Straight";
      case 5:
        return "Flush";
      case 6:
        return "Full House";
      case 7:
        return "Four of a Kind";
      case 8:
        return "Straight Flush";
      default:
        return "No Hand";
    }
  }
  
}
