import java.awt.geom.Point2D;
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

        System.out.println("players.size(): " + players.size());

        ArrayList<Integer> playerIndex = new ArrayList<>();

        //Get index of each player
        for (int i = 0; i < numPlayers; i++) {
            //Store integer referring to each player
            playerIndex.add(i);
            //Initialise players with null players
            players.add(null);
        }

        for (int i = 0; i < numPlayers; i++) {
            //Generate random number
            Random rand = new Random();
            int intRandom = rand.nextInt(playerIndex.size());

            //Use random numbers to set turn order for current player
            players.set(playerIndex.get(intRandom), new Player(Colour.values()[i]));
            playerIndex.remove(intRandom);
        }
    }

    public void run() {
        state = State.TERRITORY_SELECTION;

        if (autoSelection) {
            assignTerritories(players);
            state = State.TROOP_DEPLOYMENT;
        }
    }

    public boolean deployUnit(Point2D coordinate) {
        Territory territory = clickedTerritory(coordinate);

        //If valid territory was clicked
        if (territory != null) {
            //If players are manually selecting their territories
            if (state == State.TERRITORY_SELECTION) {
                //If the territory is not owned by another player
                if (owner(territory) == null) {
                    //Add a unit to the territory
                    territory.incrementUnits();
                    //and add the the player's list of territories
                    players.get(turn).addTerritory(territory);

                    //Sets turn to next player
                    updateTurn();

                    System.out.println(players.get(turn).getColour() + " Player's Turn!");
                    //If all territories are occupied, switch to troop deployment
                    if (worldOccupied()) {
                        state = State.TROOP_DEPLOYMENT;
                    }

                    return true;
                }
            //If players are deploying their initial troops
            } else if (state == State.TROOP_DEPLOYMENT) {
                //If the current player owns the territory clicked
                if (owner(territory) == players.get(turn)) {
                    //If they have troops to deploy
                    if (players.get(turn).getNumTroops() > 0) {
                        //Add unit to territory
                        territory.incrementUnits();
                        //Assign territory to player
                        players.get(turn).addTerritory(territory);
                        //Increment units troops
                        players.get(turn).decrementTroops();

                        //Sets turn to next player
                        updateTurn();

                        System.out.println(players.get(turn).getColour() + " Player's Turn!");

                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks whether every territory in the game has been occupied by a player
     * @return true if every territory has been occupied
     */
    private boolean worldOccupied() {
        for (Continent continent : continents) {
            for (Territory territory : continent.getTerritories()) {
                if (owner(territory) == null) {
                    return false;
                }
            }
        }

        return true;
    }

    private Player owner(Territory territory) {
        for (Player player : players) {
            if (player.getTerritories().containsKey(territory.getName())) {
                return player;
            }
        }

        return null;
    }

    /**
     * Returns the territory which has been clicked
     * @param coordinate - marking click on territory
     * @return valid territory if one was clicked or null if not
     */
    public Territory clickedTerritory(Point2D coordinate) {
        for (Continent continent : continents) {
            for (Territory territory : continent.getTerritories()) {
                if (territory.getBorder().contains(coordinate)) {
                    System.out.println(territory.getName() + " has been clicked!");
                    return territory;
                }
            }
        }

        return null;
    }

    /**
     * Assigns territories to players when auto assignment is enabled
     * @param players - to assign territories to
     */
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

            //Adds a unit to the territory
            territories.get(intRandom).incrementUnits();
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

    public void updateTurn() {
        turn++;

        if (turn == players.size()) {
            turn = 0;
        }
    }

    public State getState() {
        return state;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getTurn() {
        return turn;
    }
}
