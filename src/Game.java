import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game {
    private ArrayList<Continent> continents;
    private ArrayList<Player> players = new ArrayList<>();
    private boolean domination;
    private boolean autoSelection;
    private int turn;
    private State state;

    public Game(ArrayList<Continent> continents, int numPlayers, String gameMode, String territorySelection) {
        this.continents = continents;
        this.domination = gameMode.equals("World Domination");
        this.autoSelection = territorySelection.equals("Automatic");
        this.turn = 0;

        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player(Colour.values()[i]));

            System.out.println("Player: " + players.get(i).getColour().name());
        }

        for (Continent continent : continents) {
            System.out.println("Continent: " + continent.getName());
            System.out.println("Bonus: " + continent.getBonus());
            for (Territory territory : continent.getTerritories()) {
                System.out.println("    Territory: " + territory.getName());
                System.out.println("        Units: " + territory.getNumUnits());
            }
        }

        state = State.TERRITORY_SELECTION;
    }

    public void run() {
        if (autoSelection) {
            assignTerritories(players);
        } else {
            //TODO - manual assignment
        }


    }

    public void assignTerritories(ArrayList<Player> players) {
        ArrayList<Territory> territories = new ArrayList<>();

        //Add all territories to list
        for (Continent continent : continents) {
            territories.addAll(continent.getTerritories());
        }

        int playerIndex = 0;

        while (!territories.isEmpty()) {
            Random rand = new Random();
            int numTerritories = territories.size();

            //Generate random territory
            int intRandom = rand.nextInt(numTerritories);

            //Assign territory to player
            players.get(playerIndex++).addTerritory(territories.get(intRandom));

            //Remove territory from list of territories
            territories.remove(intRandom);

            //Wrap around to first player
            if (playerIndex >= players.size()) {
                playerIndex = 0;
            }
        }
    }

    public State getState() {
        return state;
    }
}
