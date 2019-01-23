import java.io.*;
import javax.swing.*;
import java.lang.Thread;
import java.util.*;
import javafx.util.Pair;
import java.util.concurrent.ThreadLocalRandom;

public class Startup {
  static Game game;

  public static void main (String[] args) {
    String userName = promptUserName();
    int opponentCount = promptOpponentCount();
    int timeLimit = promptTimeLimit();
    boolean shouldHeckle = promptShouldHeckle();
    game = new Game(userName, opponentCount, timeLimit, shouldHeckle);
    if (shouldHeckle) {
      startHecklingThread();
    }
    Logger logger = new Logger(game);
    logger.logStartTime();
    logger.logPlayerNames();
    int handNum = 1;
    while (handNum == 1 || shouldPlayAnotherGame()) {
      if (handNum != 1) {
        game.reset();
      }
      game.assignDealerAndBlinds(); 
      dealCardsAndDoBetting(logger, handNum);
      displayWinnersAndDistributeMoney(logger);
      handNum++;
    }
    logger.closeWriter();
  }

  private static void dealCardsAndDoBetting(Logger logger, int handNum) {
    game.dealToPlayers();
    logger.logCardsDealt(handNum);
    logger.logDealerAndBlinds();
    game.bet(logger);
    if (!game.allButOnePlayerInGameFolded()) {
      game.dealCard();
      game.dealCard();
      game.dealCard();
      logger.logFlop();
      game.bet(logger);
      if (!game.allButOnePlayerInGameFolded()) {
        game.dealCard();
        logger.logTurn();
        game.bet(logger);
        if (!game.allButOnePlayerInGameFolded()) {
          game.dealCard();
          logger.logRiver();
          game.bet(logger);
          if (!game.allButOnePlayerInGameFolded()) {
            game.flipPlayerCards();
          }
        }
      }
    }
  }

  private static void displayWinnersAndDistributeMoney(Logger logger) {
    // Determine and display winners and give out money
    List<Player> sidePotList = new ArrayList<Player>();
    if (game.allButOnePlayerInGameFolded()) {
      Player winner = game.getNonFoldedPlayers().get(0);
      String message = winner.getName() + " wins the hand since everyone else folded";
      logger.log(message);
      JOptionPane.showMessageDialog(null, message);
      if (game.getCurrentPotIndex() == 0) {
        winner.addMoney(game.getPotAmount());
        game.subtractFromPot(game.getPotAmount());
      } else {
        for (int i = 0; i <= game.getCurrentPotIndex(); i++) {
          winner.addMoney(game.getPotAmount(i));
          game.subtractFromPot(game.getPotAmount(i), i);
        }
      }
    } else {
      Pair<HashMap<Player, List<Card>>, String> winners;
      winners = game.getWinners(game.getNonFoldedPlayers(), game.getCommunityCards());
      if (game.getCurrentPotIndex() == 0) {
        // No side pots this round
        // Decide how many winners there are, return pot appropriately
        String message = game.getWinnerMessage(winners, game.getCurrentPotIndex());
        game.displayWinningCards(winners, game.getCurrentPotIndex());
        JOptionPane.showMessageDialog(null, message);
        int potDivider = winners.getKey().size();
        for (Map.Entry<Player, List<Card>> entry : winners.getKey().entrySet()) {
          Player winner = entry.getKey();
          winner.addMoney(game.getPotAmount() / potDivider);
        }
        
        game.subtractFromPot(game.getPotAmount());
        logger.log(game.getWinnerMessage(winners, game.getCurrentPotIndex()));
      } else {
        // There are side pots;
        // Loop through and return amounts to winner of each pot
        if (game.getPotAmount() == 0) {
          // if no money in current pot, don't consider it when checking
          game.decrementPotIndex();
        }
        
        for (int i = 0; i <= game.getCurrentPotIndex(); i++) {
          for (Player player : game.getNonFoldedPlayers()) {
            if (player.checkHasBetThisPot(i)) {
              sidePotList.add(player);
            }
          }
          
          winners = game.getWinners(sidePotList, game.getCommunityCards());
          String message = game.getWinnerMessage(winners, i);
          game.displayWinningCards(winners, i);
          JOptionPane.showMessageDialog(null, message);
          int potDivider = winners.getKey().size();
          for (Map.Entry<Player, List<Card>> entry : winners.getKey().entrySet()) {
            Player winner = entry.getKey();
            winner.addMoney(game.getPotAmount(i) / potDivider);
          }
          
          game.subtractFromPot(game.getPotAmount(i), i);
          logger.log(game.getWinnerMessage(winners, i));
          sidePotList.clear();
        }
      }
    }
    Player lastPlayer = game.lastPlayerWithMoneyLeft();
    if (lastPlayer != null) {
      String message = lastPlayer.getName() + " wins the game due to being the only player with money left";
      JOptionPane.showMessageDialog(null, message);
    }
    try { Thread.sleep(500); } catch (Exception e) {}
  }

  private static boolean promptShouldHeckle() {
    String message = "Would you like to enable heckling mode?";
    int answer = JOptionPane.showConfirmDialog(null, message, "", JOptionPane.YES_NO_OPTION);
    return answer == JOptionPane.YES_OPTION;
  }

  private static void startHecklingThread() {
    String[] taunts = {
        "The point of the game is to GAIN money, not lose it, silly!",
        "I know you're trying to be nice, but you really don't need to let them win.",
        "I really should have added a newbie tutorial...",
        "I take it this is your first time playing poker?",
        "What?! Why would you do that?!",
        "Maybe in a few years, you'll almost be able to hold your own at poker. (Against my dog.)",
        "Yo mamma's so stupid, she'd barely be able to beat you at poker.",
        };
    Thread heckleThread = new Thread(() -> {
      while (true) {
        int sleepTime = ThreadLocalRandom.current().nextInt(5000, 30000);
        try { Thread.sleep(sleepTime); } catch (Exception e) {}
        String taunt = taunts[new Random().nextInt(taunts.length)];
        game.displayTaunt("  Expert commentary: " + taunt);
      }
    });
    heckleThread.start();
  }

  private static boolean shouldPlayAnotherGame() {
    String message = game.lastPlayerWithMoneyLeft() == null ?
        "Would you like to play another hand?" :
        "Restart with same game parameters?";
    int answer = JOptionPane.showConfirmDialog(null, message, "", JOptionPane.YES_NO_OPTION);
    return answer == JOptionPane.YES_OPTION;
  }

  private static String promptUserName() {
    JLabel finalCardPic = new JLabel();
    try {
      String name = JOptionPane.showInputDialog("Please enter your first name:");  
      
      //minimal error checking since their name 
      //can be whatever they want it to be
      if (name.isEmpty())
        return repromptUserName();
      else
        return name;
    }
    catch (Exception e)
    {
      return repromptUserName();
    }
  }
  
  private static String repromptUserName() {
    JOptionPane.showMessageDialog(null, "Error: invalid input");
    return promptUserName();
  }

  private static int promptOpponentCount() {
    String prompt = 
      "Please enter the number of opponents from 1 to 7 (including 1 and 7).";
    try {
      int numOfOpponents = Integer.parseInt(JOptionPane.showInputDialog(prompt));
      return numOfOpponents >= 1 && numOfOpponents <= 7 ? numOfOpponents : repromptOpponentCount();
    } catch (Exception e) {
      return repromptOpponentCount();
    }
  }

  private static int repromptOpponentCount() {
    JOptionPane.showMessageDialog(null, "Error: invalid input");
    return promptOpponentCount();
  }

  private static int promptTimeLimit() {
    String prompt =
            "Please enter a player time limit in seconds, or 0 for none.";
    try {
      int timeLimit = Integer.parseInt(JOptionPane.showInputDialog(null, prompt, 0));
      return timeLimit >= 0 && timeLimit <= Integer.MAX_VALUE ? timeLimit : repromptTimeLimit();
    } catch (Exception e) {
      return repromptTimeLimit();
    }
  }

  private static int repromptTimeLimit() {
    JOptionPane.showMessageDialog(null, "Error: invalid input");
    return promptTimeLimit();
  }

}
