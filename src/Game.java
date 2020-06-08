import java.util.ArrayList;

public class Game {
    private ArrayList<Continent> continents;
    private ArrayList<Player> players = new ArrayList<>();
    private boolean domination;
    private boolean autoSelection;
    private int turn;

    public Game(ArrayList<Continent> continents, int numPlayers, String gameMode, String territorySelection) {
        this.continents = continents;
        this.domination = gameMode.equals("World Domination");
        this.autoSelection = territorySelection.equals("Automatic");
        this.turn = 0;

        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player(Colour.values()[i]));

            System.out.println("Player: " + players.get(i).getColour().name());
        }
    }

}
