import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.Border;

import java.lang.Thread;

public class Player implements Comparable<Player> {
  private final int SMALL_BLIND_BET_AMOUNT = 10;
  private final int BIG_BLIND_BET_AMOUNT = 20;
  private final int STARTING_MONEY_AMOUNT = 1000;
  private static final Color SEA_GREEN = new Color(25, 220, 95);

  private static Font font = new Font("Courier New", Font.PLAIN, 14);
  private static Font timeFont = new Font("Courier New", Font.PLAIN, 20);
  private static NumberFormat toCurrency = NumberFormat.getCurrencyInstance();
  private String name;
  private int money = STARTING_MONEY_AMOUNT;
  private Card card1;
  private Card card2;
  private boolean isComputer;
  private boolean isDealer = false;
  private boolean hasBet = false;
  private Bet bet = Bet.PENDING;
  private Game game;
  private boolean isEqualized;
  private int totalBetAmount = 0; // How much the player has already put into the pot
  private int betThisRound = 0; // Running total of the amount player has bet this round; needed for side pot calculation
  private boolean[] betThisPot = new boolean[8]; // keep track of which pot players have contributed to
  private boolean alreadyAllIn = false;
  Thread timerThread;
  PlayerTimer timer;
  private JPanel panel;
  private JLabel nameLabel;
  private JLabel moneyLabel  = new JLabel("");
  private JLabel card1Label;
  private JLabel card2Label;
  private JLabel roleLabel = new JLabel("");
  private JLabel timeLabel = new JLabel("");
  private JButton raiseButton = new JButton("Raise");
  private JButton callButton = new JButton("Call");
  private JButton foldButton = new JButton("Fold");
  private boolean hasBetThisRound = false;
  
  public Player(String name, boolean isComputer, Game game, JPanel gamePanel) {
    this.name = name;
    this.isComputer = isComputer;
    this.game = game;

    // Make Panel
    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(SEA_GREEN);
    panel.add(Box.createRigidArea(new Dimension(0, 20)));
    gamePanel.add(panel);
    panel.setVisible(true);

    // Add name
    nameLabel = new JLabel(name);
    nameLabel.setFont(font);
    panel.add(nameLabel);

    // Add money
    moneyLabel.setText(toCurrency.format(money));
    moneyLabel.setFont(font);
    panel.add(moneyLabel);

    // Add player role (e.g. dealer, big blind, and little blind)
    moneyLabel.setFont(font);
    panel.add(roleLabel);
    
    betThisPot[0] = true;  // Everyone bets in the main pot first
    for (int i = 1; i < 8; i++) {
      betThisPot[i] = false;  // Side pots default to false
    }

    // Add cards
    JPanel cardPanel = new JPanel();
    panel.add(cardPanel);
    cardPanel.setVisible(true);
    cardPanel.setBackground(SEA_GREEN);
    card1Label = new JLabel();
    card1Label.setIcon(new ImageIcon(game.makeOutline()));
    card2Label = new JLabel();
    card2Label.setIcon(new ImageIcon(game.makeOutline()));
    cardPanel.add(card1Label);
    cardPanel.add(card2Label);
    
    // Add betting buttons and time limit (if any) for user
    if (!isComputer) {
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
      buttonPanel.setBackground(SEA_GREEN);
      buttonPanel.add(Box.createRigidArea(new Dimension(25, 0)));
      
      ActionListener buttonListener = new ButtonListener();
      raiseButton.addActionListener(buttonListener);
      buttonPanel.add(raiseButton);
      buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
      
      buttonListener = new ButtonListener();
      callButton.addActionListener(buttonListener);
      buttonPanel.add(callButton);
      buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
      
      buttonListener = new ButtonListener();
      foldButton.addActionListener(buttonListener);
      buttonPanel.add(foldButton);
      buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));

      // time limit display
      if (game.getPlayerTimeLimit() != 0) {
        timeLabel.setText(String.valueOf(game.getPlayerTimeLimit()));
        timeLabel.setFont(timeFont);
        buttonPanel.add(timeLabel);
      }

      cardPanel.add(buttonPanel);
      buttonPanel.setVisible(true);
    }
  }
  
  // Used for unit testing purposes on non-GUI Player methods
  public Player(String name) {
    this.name = name;
  }
  
  public String getPlayerName() {
	  return name;
  }
  
  public void setCard1(Card card) {
    this.card1 = card;
    card1Label.setIcon(new ImageIcon(card.makeImage(!isComputer)));
    card1Label.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
  }
  
  public void setCard2(Card card) {
    this.card2 = card;
    card2Label.setIcon(new ImageIcon(card.makeImage(!isComputer)));
    card2Label.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
  }

  public String getName() {
    return name;
  }
  
  // Display an outline in place of cards to represent a player has
  // folded this hand
  public void displayFold() {
    card1Label.setIcon(new ImageIcon(game.makeOutline()));
    card2Label.setIcon(new ImageIcon(game.makeOutline()));
  }
  
  // Flips this player's cards
  // Needed during the showdown
  public void flipCards() {
    card1Label.setIcon(new ImageIcon(card1.makeImage(true)));
    card2Label.setIcon(new ImageIcon(card2.makeImage(true)));
  }
  
  //Paints yellow border around the card or cards in the player's hand that
  //contributed to their winning hand
  public void displayWinningCards (List<Card> cards) {
	  Border border = BorderFactory.createLineBorder(Color.YELLOW, 5);
	  if (cards.contains(card1)) {
		  card1Label.setBorder(border);
		  card1Label.repaint();
		  card1Label.revalidate();
	  }
	  if (cards.contains(card2)) {
		  card2Label.setBorder(border);
		  card2Label.repaint();
		  card2Label.revalidate();
	  }
  }
  
  //blanks out the border that indicates a card was part of a winning hand
  public void removeCardBorder() {
	  card1Label.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	  card2Label.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
  }
  public void goAllIn() {
    bet = Bet.ALL_IN;
  }
  
  public boolean getIsAllIn() {
    return bet == Bet.ALL_IN;
  }
  
  public void setHasBetThisPot(int index, boolean flag) {
    betThisPot[index] = flag;
  }
  
  public boolean[] getHasBetThisPot() {
    return betThisPot;
  }
  
  public boolean checkHasBetThisPot(int index) {
    return betThisPot[index];
  }
  
  public int getTotalBetAmount() {
    return totalBetAmount;
  }
  
  public void resetBetThisRound() {
    betThisRound = 0;
  }
  
  public int getBetThisRound() {
    return betThisRound;
  }
  
  public void setAlreadyAllIn(boolean value) {
    alreadyAllIn = value;
  }
  
  public boolean getAlreadyAllIn() {
    return alreadyAllIn;
  }
  
  // Need to set player's bet to PENDING at end of hand
  public void setCurrentBet(Bet bet) {
    this.bet = bet;
  }
  
  public Bet getCurrentBet() {
    return bet;
  }
  
  public boolean getHasBetThisRound () {
	  return hasBetThisRound;
  }
  
  public void setHasBetThisRound (boolean value) {
	  hasBetThisRound = value;
  }
  
  /*
   * checks through all the non-folded players to see if any player has bet yet this round
   * if at least one player has bet, then the button's text is kept "Call"
   * but if no player has bet money this betting round, the text is changed
   * to "Check" whenever it is the user's turn to bet
   */
  public void checkCallButtonText() {
	  boolean changeText = true;
	  for (Player player : game.getNonFoldedPlayers()) {
		  if(player.hasBetThisRound) {
			  changeText = false;
		  }
	  }
	  
	  if (changeText && !this.isComputer) {
		  callButton.setText("Check");
	  }
	  else if (!changeText && !this.isComputer){
		  callButton.setText("Call");
	  }
  }
  
  public void bet(boolean previouslyBetThisRound, Logger logger) {
    if (!game.getHasSmallBlindBet() && this == game.getLittleBlind()) {
      if (money < SMALL_BLIND_BET_AMOUNT) {
        goAllIn2();
      } else {
        raise(SMALL_BLIND_BET_AMOUNT);
      }
      game.setHasSmallBlindBet(true);
    } else if (!game.getHasBigBlindBet() && this == game.getBigBlind()) {
      if (money < BIG_BLIND_BET_AMOUNT) {
        goAllIn2();
      } else {
        raise(BIG_BLIND_BET_AMOUNT);
      }
      game.setHasBigBlindBet(true);
    } else if (isComputer) {
      doComputerBet(previouslyBetThisRound);
    } else {
      doUserBet(previouslyBetThisRound, logger);
    }
    isEqualized = true;
    logger.logBet(this);
  }


  private void doUserBet(boolean previouslyBetThisRound, Logger logger) {
    // start timer thread
    if (game.getPlayerTimeLimit() != 0) {
      timer = new PlayerTimer();
      timerThread = new Thread(timer);
      timerThread.start();
    }
    waitForUserAction();
    switch (bet) {
      case ALL_IN:
        break;
      case FOLD:
        fold();
        break;
      case CALL:
        call();
        break;
      case RAISE:
        int raiseAmount = promptRaiseAmount();
        if (raiseAmount == -1) {  // Returns -1 if user hits "cancel" in dialog box
          bet(previouslyBetThisRound, logger);
        } else {
          raise(raiseAmount);
        }
        break;
    }
  }

  void doComputerBet(boolean previouslyBetThisRound) {
    Random rand = new Random();
    Bet[] choices = new Bet[]{Bet.RAISE, Bet.CALL, Bet.FOLD};
    // If money has been bet => raise, call, fold
    if (previouslyBetThisRound) {
      int choice = rand.nextInt(choices.length);
      switch (choices[choice]) {
        case RAISE:
          int possibleRaise = 0;
          // Returns -1 if player doesn't have enough money to raise
          possibleRaise = getComputerRaise();
          if (possibleRaise == -1) {
            call();
          } else {
            raise(possibleRaise);
          }
          break;
        case CALL:
          call();
          break;
        case FOLD:
          fold();
          break;
      }
    } else { // If no money has been bet => raise, call
      int choice = rand.nextInt(choices.length - 1); // Don't make fold a possibility
      switch (choices[choice]) {
        case RAISE:
          int possibleRaise = getComputerRaise();
          // Returns -1 if player doesn't have enough money to raise
          if (possibleRaise == -1) {
            call();
          } else {
            raise(possibleRaise);
          }
          break;
        case CALL:
          call();
          break;
      }
    }
  }

  // NOTICE: this method doesn't change if isEqualized.
  private void goAllIn2() {
    bet = Bet.ALL_IN;
    betThisRound += money;
    sendToPot(money);
    if (game.getHighestBet() < totalBetAmount) {
        game.setHighestBet(totalBetAmount);
    }
  }

  private void call() {
    bet = Bet.CALL;
    // Player goes all-in by calling a bet that is more than they have in their stack 
    // plus what they already have in the pot
    if (game.getHighestBet() >= money + totalBetAmount) {
      betThisRound += money;
      bet = Bet.ALL_IN;
      sendToPot(money);
    } else {
      betThisRound += (game.getHighestBet() - totalBetAmount);
      sendToPot(game.getHighestBet() - totalBetAmount);
    }
  }

  private void raise(int newHighestBet) {
    bet = Bet.RAISE;
    // Player can also go all-in by raising to the amount they have in their stack
    // plus what they already have in the pot
    if (newHighestBet == money + totalBetAmount) {
      bet = Bet.ALL_IN;
    }
    betThisRound += (newHighestBet - totalBetAmount);
    sendToPot(newHighestBet - totalBetAmount);
    game.setHighestBet(newHighestBet);
    
    for (Player player : game.getNonFoldedPlayers()) {
      if (player.money != 0) {
        player.isEqualized = false;
      }
    }
    this.hasBetThisRound = true;
  }

  private void fold() {
    bet = Bet.FOLD;
    displayFold();
  }
  

  // Wait for the user to specify what action to take and puts the result in this.bet
  private void waitForUserAction() {
    while (!hasBet) {
      try {
        Thread.sleep(100);
      } catch (Exception e) {}
    }
    hasBet = false;
  }

  public int getMoney() {
    return money;
  }
  
  // Subtract amount from the player's stack
  // Also update player's money label
  public void subtractMoney(int amount) {
    money -= amount;
  }
  
  // Add amount to the player's stack
  // Also update player's money label
  public void addMoney(int amount) {
    money += amount;
  }

  public void refundBet(int amount) {
    money += amount;
    totalBetAmount -= amount;
  }
  
  public Card getCard1() {
	  return card1;
  }
  
  public Card getCard2() {
	  return card2;
  }
  
  class ButtonListener implements ActionListener {
    
    public void actionPerformed(ActionEvent e) {
      JButton source = (JButton) e.getSource();
	    String currentText = source.getText();
      if (!hasBet) {
        switch (currentText) {
          case "Raise":
            bet = Bet.RAISE;
            break;
          case "Call":
            bet = Bet.CALL;
            break;
          case "Check":
            bet = Bet.CALL;
            break;
          case "Fold":
            bet = Bet.FOLD;
            break;
        }
        if (game.getPlayerTimeLimit() != 0) {
          timerThread.interrupt(); // stop the timer thread
        }
        hasBet = true;
      }
    }
  }
  
  private void sendToPot(int amount) {
    game.addToPot(amount);
    money -= amount;
    totalBetAmount += amount;
  }

  private int promptRaiseAmount() {
    String prompt = "Enter the amount you want to raise to:";
    try {
      String userEntry = JOptionPane.showInputDialog(prompt);
      if (userEntry == null) {
        return -1;
      }
      int raise = Integer.parseInt(userEntry);
      int moneyToSendToPot = raise - totalBetAmount;
      boolean isValid = raise > game.getHighestBet() && moneyToSendToPot <= money;
      return  isValid ? raise : repromptRaiseAmount();
    } catch (Exception e) {
      return repromptRaiseAmount();
    }
  }

  // Return a valid random amount for a computer player to raise to
  private int getComputerRaise() {
    if (game.getHighestBet() >= money + totalBetAmount) {
      // Player cannot raise if they don't have enough money;
      // So will instead call the current bet (should put them all in?)
      return -1;
    }
    Random rand = new Random();
    int high = (money + totalBetAmount) + 1;
    int low = game.getHighestBet() + 1;
    return rand.nextInt(high - low) + low;
  }

  private int repromptRaiseAmount() {
    JOptionPane.showMessageDialog(null, "Error: invalid input. Note that you can't raise by more\n" +
                                        "money than you have, there's no betting of cents,\n" + 
                                        "and you must raise to an amount higher than the\n" +
                                        "current highest bet. If there is no current bet, the\n" +
                                        "minimum bet you can raise to is $20.");
    return promptRaiseAmount();
  }

  public boolean getIsDealer() {
    return isDealer;
  }

  public void setIsDealer(boolean isDealer) {
    this.isDealer = isDealer;
  }

  public boolean getIsEqualized() {
    return isEqualized;
  }

  public void setIsEqualized(boolean isEqualized) {
    this.isEqualized = isEqualized;
  }

  public void resetBetting() {
    totalBetAmount = 0;
    bet = Bet.PENDING;
    isEqualized = !inGame();
    alreadyAllIn = false;
  }

  public boolean hasMoney() {
    return money != 0;
  }
  
  // Sorts by a Players betThisRound (then by money in case of ties) in ascending order
  @Override
  public int compareTo(Player other) {
    int ret = Integer.compare(betThisRound, other.betThisRound);
    if (ret != 0) {
      return ret;
    }
    return Integer.compare(money, other.money);
  }

  public void updateDisplay() {
    moneyLabel.setText(toCurrency.format(money));
    if (!inGame()) {
      card1Label.setIcon(new ImageIcon(game.makeOutline()));
      card2Label.setIcon(new ImageIcon(game.makeOutline()));
      roleLabel.setText("Player out of game");
    } else {
      String role = this == game.getLittleBlind() ? "Little Blind" :
                     this == game.getBigBlind() ? "Big Blind" :
                     "";
      if (isDealer) {
        role += " Dealer";
      }
      roleLabel.setText(role);
    }
    moneyLabel.setText(toCurrency.format(money));
    card1Label.repaint();
    card1Label.revalidate();
    card2Label.repaint();
    card2Label.revalidate();
  }

  public void resetMoney() {
    money = STARTING_MONEY_AMOUNT;
  }

  public boolean inGame() {
    return money > 0 || bet == Bet.ALL_IN;
  }

  // runnable timer class to use with each thread
  private class PlayerTimer implements Runnable {
    // countdown timer thread handling
    // called when new thread is created on this object
    public void run() {
      for (int i = game.getPlayerTimeLimit(); i >= 0 && !hasBet; i--) {
        timeLabel.setText(String.valueOf(i));
        try {
          Thread.sleep(1000);
        } catch (InterruptedException iex) {
          // ignore
        }
      }
      if (!hasBet) {
        hasBet = true;
        bet = Bet.FOLD;
      }

    }
  }

}
