import java.awt.*;
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
    private TacticalMovePhase tacticalMovePhase = new TacticalMovePhase();
    private final Point2D[] redDiceCoords = {new Point2D.Double(125, 510), new Point2D.Double(225, 510)};
    private Rectangle2D attackButton = new Rectangle2D.Double(123, 738, 95, 95);
    private Rectangle turnButton = new Rectangle(70, 20, 120, 80);
    private Dice attackingDice = new Dice();
    private Dice defendingDice = new Dice();

    public Game(ArrayList<Continent> continents, int numPlayers, String gameMode, String territorySelection) {
        this.continents = continents;
        this.domination = gameMode.equals("World Domination");
        this.autoSelection = territorySelection.equals("Automatic");
        this.turn = 0;

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

    //TODO - split into different functions
    public boolean tacticalMovePhase(Point2D coordinate) {
        //If next turn button clicked
        if (nextPhase(coordinate)) {
            //Update turn and switch state to reinforcements
            updateTurn();
            state = State.REINFORCEMENTS;
            //calculate number of troops for new player
            players.get(turn).setNumTroops(calculateTroops(players.get(turn).getTerritories()));
            return false;
        }

        Territory territory = clickedTerritory(coordinate);

        //If territory was clicked
        if (territory != null) {
            //Get the owner of that territory
            Player owner = owner(territory);

            switch (tacticalMovePhase.getStage()) {
                case NONE_SELECTED:
                    //If the selected country is owned by the current player and has more than 1 unit in it
                    if (owner == players.get(turn) && territory.getNumUnits() > 1) {
                        //Mark this territory as the attacking territory and move to next stage of attack
                        tacticalMovePhase.setStage(MoveStage.SOURCE_SELECTED);
                        tacticalMovePhase.setSource(territory);
                    }
                    break;
                case SOURCE_SELECTED:
                    //If the source territory is clicked again
                    if (territory == tacticalMovePhase.getSource()) {
                        //Deselect it by moving back to previous stage
                        tacticalMovePhase.setStage(MoveStage.NONE_SELECTED);
                        //Otherwise if a friendly territory has been selected which is also adjacent to the attacking territory
                    } else if (owner == players.get(turn) && tacticalMovePhase.getSource().getAdjacent().contains(territory.getName())) {
                        //Select destination territory
                        tacticalMovePhase.setStage(MoveStage.DESTINATION_SELECTED);
                        tacticalMovePhase.setDestination(territory);
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    //TODO split into different functions
    public boolean attack(Point2D coordinate) {
        //If next phase button clicked, then switch to tactical move phase
        if (nextPhase(coordinate)) {
            state = State.TACTICAL_MOVE_PHASE;
            return false;
        }

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
                    //Reset values of attacking and defending dice
                    attackingDice.init();
                    defendingDice.init();

                    //If the selected country is owned by the current player and has more than 1 unit in it
                    if (owner == players.get(turn) && territory.getNumUnits() > 1) {
                        //Mark this territory as the attacking territory and move to next stage of attack
                        attackPhase.setStage(AttackStage.ATTACKER_SELECTED);
                        attackPhase.setAttacking(territory);
                    }
                    break;
                //If the attacking territory has been selected
                case ATTACKER_SELECTED:
                    //If the attacking territory is clicked again
                    if (territory == attackPhase.getAttacking()) {
                        //Deselect it by moving back to previous stage
                        attackPhase.setStage(AttackStage.NONE_SELECTED);
                    //Otherwise if an enemy territory has been selected which is also adjacent to the attacking territory
                    } else if (owner != players.get(turn) && attackPhase.getAttacking().getAdjacent().contains(territory.getName())) {
                        //Move to next stage
                        attackPhase.setStage(AttackStage.DEFENDER_SELECTED);
                        //Set defender to enemy territory
                        attackPhase.setDefending(territory);
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
                    //returns true if attack is to be launched
                    return true;
            }
        }

        return false;
    }

    private boolean nextPhase(Point2D click) {
        return turnButton.contains(click);
    }

    /**
     * Carry out attack from one country to another
     */
    public void launchAttack() {
        Territory attackingTerr = attackPhase.getAttacking();
        Territory defendingTerr = attackPhase.getDefending();
        Player attackingPlayer = owner(attackingTerr);
        Player defendingPlayer = owner(defendingTerr);

        rollDice();

        ArrayList<Integer> casualties = Dice.compare(attackingDice, defendingDice);
        attackingPlayer.inflictCasualties(attackingTerr, casualties.get(0));
        defendingPlayer.inflictCasualties(defendingTerr, casualties.get(1));

        //TODO - update state to win if defender has 0 troops remaining

        int attackingUnits = attackingPlayer.getTerritories().get(attackingTerr.getName()).getNumUnits();
        int defendingUnits = defendingPlayer.getTerritories().get(defendingTerr.getName()).getNumUnits();

        updateNumDice(attackingUnits, defendingUnits);
    }

    /**
     * Transfer troops from first territory to second territory
     * @param location - to display dialogue window
     */
    public void transferTroops(Point2D location) {
        Territory source = tacticalMovePhase.getSource();
        Territory destination = tacticalMovePhase.getDestination();

        String msg = "Select Number of Troops to Move to Territory:";
        String title = "Tactical Move Phase";

        int min = 2;
        int max = source.getNumUnits() - 1;

        int numTroops = TroopTransferDialogue.display(title, msg, min, max, true, location);

        //If cancel was selected
        if (numTroops == 0) {
            tacticalMovePhase.setStage(MoveStage.NONE_SELECTED);
        } else {
            //Add units to destination territory
            players.get(turn).getTerritories().get(destination.getName()).setNumUnits(numTroops);

            //Remove troops in new territory from old territory
            int newUnits = source.getNumUnits() - numTroops;
            players.get(turn).getTerritories().get(source.getName()).setNumUnits(newUnits);

            tacticalMovePhase.setStage(MoveStage.NONE_SELECTED);

            //Update turn and switch state to reinforcements
            updateTurn();
            //calculate number of troops for new player
            players.get(turn).setNumTroops(calculateTroops(players.get(turn).getTerritories()));
            state = State.REINFORCEMENTS;
        }
    }

    /**
     * Transfer defending territory to attacker and garrison it using troops from the attacking territory
     * @param location - to display dialogue window
     */
    public void transferTerritory(Point2D location) {
        Territory attackingTerritory = attackPhase.getAttacking();
        Territory defendingTerritory = attackPhase.getDefending();
        Player attacker = owner(attackingTerritory);
        Player defender = owner(defendingTerritory);

        String msg = "Select Number of Troops to Occupy Conquered Territory:";
        String title = "Victory!";
        int min = attackPhase.getRedDice();
        int max = attackingTerritory.getNumUnits() - 1;

        int numTroops = (max > min) ? TroopTransferDialogue.display(title, msg, min, max, false, location) : max;

        //Remove territory from defending player
        defender.getTerritories().remove(defendingTerritory.getName());
        //Set num units of territory equal to number of units selected by attacker
        defendingTerritory.setNumUnits(numTroops);
        //Assign territory to attacker
        attacker.addTerritory(defendingTerritory);

        //Remove troops in new territory from old territory
        int newUnits = attackingTerritory.getNumUnits() - numTroops;
        attacker.getTerritories().get(attackingTerritory.getName()).setNumUnits(newUnits);
    }

    public void resetAttackPhase() {
        //Change to correct state
        attackPhase.setStage(AttackStage.NONE_SELECTED);
        //Reset number of dice
        attackPhase.setRedDice(1);
        attackPhase.setWhiteDice(1);
    }

    /**
     * Rolls the attacking and defending dice
     */
    public void rollDice() {
        //Roll required number of dice for attacker and defender
        attackingDice.roll(attackPhase.getRedDice());
        defendingDice.roll(attackPhase.getWhiteDice());
    }

    /**
     * Updates the number of dice used by the attacker and defender based on the new number of units
     * @param attackingUnits
     * @param defendingUnits
     */
    private void updateNumDice(int attackingUnits, int defendingUnits) {
        //Update the number of attacking dice
        switch (attackingUnits) {
            //If 1 attacker left, attack must end
            case 1:
                attackPhase.setStage(AttackStage.NONE_SELECTED);
                attackPhase.setRedDice(1);
                attackPhase.setWhiteDice(1);
                break;
            //If two attackers left, can still attack with one dice
            case 2:
                switch (attackPhase.getStage()) {
                    case TWO_DICE:
                    case THREE_DICE:
                        attackPhase.setStage(AttackStage.DEFENDER_SELECTED);
                        attackPhase.setRedDice(1);
                        attackPhase.setWhiteDice(1);
                }
                break;
            //If three attackers left, can attack with two dice
            case 3:
                switch (attackPhase.getStage()) {
                    case THREE_DICE:
                        attackPhase.setStage(AttackStage.TWO_DICE);
                        attackPhase.setRedDice(2);
                }

                break;
        }

        //Update the number of defending dice
        switch (attackPhase.getStage()) {
            //If attacker is rolling with two or three dice...
            case TWO_DICE:
            case THREE_DICE:
                //...and defender has more than 1 unit in their territory
                if (defendingUnits > 1) {
                    //Then the defender can roll two dice
                    attackPhase.setWhiteDice(2);
                    break;
                }
            default:
                //Otherwise they must roll 1 dice
                attackPhase.setWhiteDice(1);
        }
    }

    public Dice getAttackingDice() {
        return attackingDice;
    }

    public Dice getDefendingDice() {
        return defendingDice;
    }

    public boolean attackButtonClicked(Point2D coordinate) {
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

    /**
     * Calculates number of reinforcements the player receives
     * at the start of their turn
     * @param territories - used to calculate number of starting troops
     * @return - number of troops
     */
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

    /**
     * Updates turn by incrementing turn counter to next player
     */
    public void updateTurn() {
        //Increment turn counter
        turn++;

        //If end of list of players has been reached...
        if (turn == players.size()) {
            //...wrap around to first player
            turn = 0;
        }

        //If player has no more territories, update turn again
        if (players.get(turn).getTerritories().size() == 0) {
            updateTurn();
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

    public TacticalMovePhase getTacticalMovePhase() {
        return tacticalMovePhase;
    }
}
