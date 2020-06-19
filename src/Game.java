import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Random;

public class Game {
    private ArrayList<Continent> continents;
    private ArrayList<Player> players = new ArrayList<>();
    private boolean domination;
    private boolean autoSelection;
    private int turn;
    private State state;
    private AttackPhase attackPhase = new AttackPhase();
    private final Point2D[] redDiceCoords = {new Point2D.Double(125, 510), new Point2D.Double(225, 510)};
    private Rectangle2D attackButton = new Rectangle2D.Double(123, 738, 95, 95);

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

    //TODO split into different functions
    public void attack(Point2D coordinate) {
        Territory territory = clickedTerritory(coordinate);
        Rectangle2D secondDice = new Rectangle2D.Double(redDiceCoords[0].getX(), redDiceCoords[0].getY(), 100, 101);
        Rectangle2D thirdDice = new Rectangle2D.Double(redDiceCoords[1].getX(), redDiceCoords[1].getY(), 100, 101);

        //If coordinate lies within a territory
        if (territory != null) {
            //Get the owner of that territory
            Player owner = owner(territory);

            //Switch based on stage of attack phase
            switch (attackPhase.getStage()) {
                //If no countries have been selected
                case NONE_SELECTED:
                    //If the selected country is owned by the current player and has more than 1 unit in it
                    if (owner == players.get(turn) && territory.getNumUnits() > 1) {
                        //Mark this territory as the attacking territory and move to next stage of attack
                        attackPhase.setStage(AttackStage.ATTACKER_SELECTED);
                        attackPhase.setAttacking(territory);
                        System.out.println("Attacking from " + territory.getName());
                    }
                    break;
                //If the attacking territory has been selected
                case ATTACKER_SELECTED:
                    //If the attacking territory is clicked again
                    if (territory == attackPhase.getAttacking()) {
                        //Deselect it by moving back to previous stage
                        attackPhase.setStage(AttackStage.NONE_SELECTED);
                    //Otherwise if an enemy territory has been selected
                    } else if (owner != players.get(turn)) {
                        //Move to next stage
                        attackPhase.setStage(AttackStage.DEFENDER_SELECTED);
                        //Set defender to enemy territory
                        attackPhase.setDefending(territory);
                        System.out.println(territory.getName() + " is defending");
                    }
                    break;
                case DEFENDER_SELECTED:
                case TWO_DICE:
                case THREE_DICE:
                    //If defending territory was clicked again
                    if (territory == attackPhase.getDefending()) {
                        //Move to previous stage (effectively deselecting defender)
                        attackPhase.setStage(AttackStage.ATTACKER_SELECTED);
                        //Set number of dice in each case to 1
                        attackPhase.setRedDice(1);
                        attackPhase.setWhiteDice(1);
                    }
                    break;
            }
        //Otherwise if second dice clicked
        } else if (diceClicked(coordinate, secondDice)) {
            switch (attackPhase.getStage()) {
                //Add new dice if only first dice in use
                case DEFENDER_SELECTED:
                    if (attackPhase.getAttacking().getNumUnits() > 2) {
                        attackPhase.setStage(AttackStage.TWO_DICE);

                        attackPhase.incrementRed();

                        if (attackPhase.getDefending().getNumUnits() > 1) {
                            attackPhase.incrementWhite();
                        }
                    }
                    break;
                //Get rid of second dice if both in use
                case TWO_DICE:
                    attackPhase.setStage(AttackStage.DEFENDER_SELECTED);
                    attackPhase.decrementRed();

                    if (attackPhase.getWhiteDice() == 2) {
                        attackPhase.decrementWhite();
                    }
                    break;
            }
        //Otherwise if third dice clicked
        } else if (diceClicked(coordinate, thirdDice)) {
            switch (attackPhase.getStage()) {
                //Add new dice if only two in use
                case TWO_DICE:
                    if (attackPhase.getAttacking().getNumUnits() > 3) {
                        attackPhase.setStage(AttackStage.THREE_DICE);
                        attackPhase.incrementRed();
                    }
                    break;
                case THREE_DICE:
                    attackPhase.setStage(AttackStage.TWO_DICE);
                    attackPhase.decrementRed();
                    break;
            }
        //If attack button clicked when dice selected
        } else if (attackButtonClicked(coordinate)) {
            switch (attackPhase.getStage()) {
                case DEFENDER_SELECTED:
                case TWO_DICE:
                case THREE_DICE:
                    Player attacker = owner(attackPhase.getAttacking());
                    Player defender = owner(attackPhase.getDefending());
                    ArrayList<Integer> attackingDice = new ArrayList<>();
                    ArrayList<Integer> defendingDice = new ArrayList<>();

                    Random rand = new Random();

                    //Roll attacking dice
                     while (attackingDice.size() < attackPhase.getRedDice()) {
                         attackingDice.add(rand.nextInt(6) + 1);
                     }

                     //Roll defending dice
                     while (defendingDice.size() < attackPhase.getWhiteDice()) {
                         defendingDice.add(rand.nextInt(6) + 1);
                     }

                     //Sort dice in ascending order
                    Collections.sort(attackingDice);
                    Collections.sort(defendingDice);

                    int a = attackingDice.size() - 1;
                    int d = defendingDice.size() - 1;

                    Territory attacking = attackPhase.getAttacking();
                    Territory defending = attackPhase.getDefending();

                    //Remove soldier from losing territory
                    while (d >= 0) {
                        //If attacker wins, remove one soldier from defending territory
                        if (attackingDice.get(a) > defendingDice.get(d)) {
                            defender.getTerritories().get(defending.getName()).decrementUnits();
                        //If defender wins, remove one soldier from attacking territory
                        } else {
                            attacker.getTerritories().get(attacking.getName()).decrementUnits();
                        }

                        a--;
                        d--;

                        //TODO - create dice objects within game object
                        //TODO - use dice object values to update dice pictures on board
                        //TODO - update state after attack
                    }

                    System.out.println("Attacking dice: " + attackingDice);
                    System.out.println("Defending dice: " + defendingDice);
                    //TODO - implement dice rolling animation
            }
        }
    }

    private boolean attackButtonClicked(Point2D coordinate) {
        return attackButton.contains(coordinate.getX(), coordinate.getY());
    }

    private boolean diceClicked(Point2D coordinate, Rectangle2D dice) {
        return dice.contains(coordinate.getX(), coordinate.getY());
    }

    //TODO - break down to smaller functions
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
            } else if (state == State.TROOP_DEPLOYMENT || state == State.REINFORCEMENTS) {
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
                        if (state == State.TROOP_DEPLOYMENT) {
                            updateTurn();
                            System.out.println(players.get(turn).getColour() + " Player's Turn!");


                            if (troopsDeployed()) {
                                //Move to reinforcement phase
                                state = State.REINFORCEMENTS;
                                //calculate number of troops for player
                                players.get(turn).setNumTroops(calculateTroops(players.get(turn).getTerritories()));
                            }
                        //If reinforcements deployed then switch to attack phase
                        } else if (state == State.REINFORCEMENTS) {
                            if (troopsDeployed()) {
                                state = State.ATTACK_PHASE;
                            }
                        }

                        return true;
                    } else if (state == State.REINFORCEMENTS) {
                        state = State.ATTACK_PHASE;
                    }
                }
            }
        }

        return false;
    }

    private int calculateTroops(LinkedHashMap<String, Territory> territories) {
        //Calculate number of troops based on number of territories
        int troops = (territories.size() < 9) ? 3 : Math.floorDiv(territories.size(), 3);

        //Add bonuses provided by controlled continents
        for (Continent continent : continents) {
            boolean bonus = true;
            for (Territory territory : continent.getTerritories()) {
                if (!territories.containsKey(territory.getName())) {
                    bonus = false;
                    break;
                }
            }

            if (bonus) {
                troops += continent.getBonus();
            }
        }

        return troops;
    }

    private boolean troopsDeployed() {
        for (Player player : players) {
            if (player.getNumTroops() > 0) {
                return false;
            }
        }

        return true;
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

    public AttackPhase getAttackPhase() {
        return attackPhase;
    }
}
