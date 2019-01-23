import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Player.java
 * Note: This only tests methods that don't deal with GUI
 */

public class PlayerTest {

  /**
   * Unit tests for addMoney()
   * Success Case: money updated to previous + arg
   * Failure Case: money equal to other value
   */
  
  @Test
  public void addMoneyTest() {
    Player player = new Player("Test");
    player.addMoney(77);
    assertEquals(1077, player.getMoney());
  }

  /**
   * Unit tests for subMoney()
   * Success Case: money updated to previous - arg
   * Failure Case: money equal to other value
   */

  @Test
  public void subMoneyTest() {
    Player player = new Player("Test");
    player.subtractMoney(50);
    assertEquals(950, player.getMoney());
  }
  
  /**
   * Unit tests for setCurrentBet()
   * Success Case: bet field updated to arg
   * Failure Case: bet field equal to anything else
   */
  
  @Test
  public void setBetTest() {
    Player player = new Player("Test");
    player.setCurrentBet(Bet.CALL);
    assertEquals(Bet.CALL, player.getCurrentBet());
  }
  
}