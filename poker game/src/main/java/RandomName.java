import java.util.*;


public class RandomName {

  //hard coded string array of names
  private static String[] names = {"Allison","Arthur","Ana","Alex","Arlene","Alberto","Barry","Bertha","Bill","Bonnie","Bret","Beryl","Chantal","Cristobal","Claudette",
      "Charley","Cindy","Chris","Dean","Dolly","Danny","Danielle","Dennis","Debby","Erin","Edouard","Erika","Earl","Emily","Ernesto","Felix","Fay","Fabian","Frances",
      "Franklin","Florence","Gabielle","Gustav","Grace","Gaston","Gert","Gordon","Humberto","Hanna","Henri","Hermine","Harvey","Helene","Iris","Isidore","Isabel","Ivan",
      "Irene","Isaac","Jerry","Josephine","Juan","Jeanne","Jose","Joyce","Karen","Kyle","Kate","Karl","Katrina","Kirk","Lorenzo","Lili","Larry","Lisa","Lee","Leslie",
      "Michelle","Marco","Mindy","Maria","Michael","Noel","Nana","Nicholas","Nicole","Nate","Nadine","Olga","Omar","Odette","Otto","Ophelia","Oscar","Pablo","Paloma",
      "Peter","Paula","Philippe","Patty","Rebekah","Rene","Rose","Richard","Rita","Rafael","Sebastien","Sally","Sam","Shary","Stan","Sandy","Tanya","Teddy","Teresa",
      "Tomas","Tammy","Tony","Van","Vicky","Victor","Virginie","Vince","Valerie","Wendy","Wilfred","Wanda","Walter","Wilma","William"};
  private List<String> availableNames = new ArrayList<String>();
  
  /*
   * Constructor that intakes the number of 
   * players the user wants to play against
   */
  public RandomName(String playerName) {
    for (int i = 0; i < names.length; i++) {
      if (!names[i].equals(playerName)) {
        availableNames.add(names[i]);
      }
    }
  }
  
  /*
   * Method to generate a list of random names.
   * Returns String array of the names.
   */
  public String getRandomName() {
    return availableNames.remove((new Random()).nextInt(availableNames.size()));
  }
}
