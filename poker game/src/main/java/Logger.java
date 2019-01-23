import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import javafx.util.Pair;

public class Logger {

	private JPanel eventPanel = new JPanel();
	private JTextArea textArea = new JTextArea();
	private JScrollPane scroll; 
	private Game game;
	private FileWriter fileWriter;
	private PrintWriter writer; 
	private File file;
	
	public Logger(Game game) {
		this.game = game;
		
		// The date is to make filename unique
		Date currentDate = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("dd-Myyyy-hh-mm-ss");
		String fileName = new String("GameLog" + ft.format(currentDate) + ".txt");
		file = new File(fileName);
		try {
			fileWriter = new FileWriter(file);
			writer = new PrintWriter(fileWriter);
		} 
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "Problem with creating file - game will not be logged");
		}
		
		// Create new panel for event display
		eventPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		game.getFrame().add(eventPanel, BorderLayout.SOUTH);
		eventPanel.setPreferredSize(new Dimension(game.getFrame().getWidth(), 100));
		eventPanel.setLayout(new BoxLayout(eventPanel, BoxLayout.X_AXIS));
		eventPanel.setBackground(new Color(0,0,0));
		// Add text area to scroll pane
		scroll = new JScrollPane(textArea);
		// Add scroll pane with text area to event panel
		eventPanel.add(scroll);
	    eventPanel.setVisible(true);
	}

	public void logStartTime() {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("dd M yyyy 'at' hh:mm:ss a");
		log("Game Started: " + ft.format(dNow));
  }

  public void logPlayerNames() {
		log("Player Name: " + game.getPlayers().get(0).getPlayerName());
		StringBuilder aiNames = new StringBuilder("");
		
		for (Player player : game.getPlayers()) {
			if (player != game.getPlayers().get(0)){
				aiNames.append(player.getPlayerName() + ", ");
			}
			
		}
		
		aiNames.setLength(aiNames.length() - 2);
		log("AI Players: " + aiNames.toString());
	}
	
	public void logCardsDealt(int handNum) {
		log("\nHand: " + handNum);
		log("Cards Dealt:");
		for (Player player : game.getPlayers()) {
			if(player == game.getPlayers().get(0))
			{
				textArea.append(player.getPlayerName() + ": " + player.getCard1().toString() + " and " + player.getCard2().toString() + "\n");
				writer.println(player.getPlayerName() + ": " + player.getCard1().toString() + " and " + player.getCard2().toString());
			}
			else
			{
				writer.println(player.getPlayerName() + ": " + player.getCard1().toString() + " and " + player.getCard2().toString());
			}
			
		}
	}
	
	public void logDealerAndBlinds () {
		log("Dealer for this hand is " + game.getDealer().getPlayerName());
		log("Little Blind for this hand is " + game.getLittleBlind().getPlayerName());
		log("Big Blind for this hand is " + game.getBigBlind().getPlayerName());
	}
	
	public void logBet(Player player) {
    if (player.getCurrentBet() == Bet.RAISE) {  
      log(player.getPlayerName() + " Raises to " + game.getHighestBet());
    } else if (player.getCurrentBet() == Bet.CALL) {
      log(player.getPlayerName() + " calls.");
    } else if (player.getCurrentBet() == Bet.FOLD) {
      log(player.getPlayerName() + " folds.");

    } else if (player.getCurrentBet() == Bet.ALL_IN) {
      log(player.getPlayerName() + " is all-in.");
    }
  }
  
	public void logFlop() {
		log("Flop: " + game.getCommunityCards().get(0).toString() + ", " + game.getCommunityCards().get(1).toString() + ", " + game.getCommunityCards().get(2).toString());
	}
	
	public void logTurn() {
		log("Turn: " + game.getCommunityCards().get(3).toString());
	}
	
	public void logRiver() {
		log("River: " + game.getCommunityCards().get(4).toString());
	}
	
	public void closeWriter() {
		writer.close();
	}

  public void log(String message) {
    textArea.append(message + "\n");
    textArea.setCaretPosition(textArea.getDocument().getLength());
    writer.println(message);
  }
}
