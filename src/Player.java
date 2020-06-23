import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Player {
    private LinkedHashMap<String, Territory> territories = new LinkedHashMap<>();
    private Colour colour;
    private ArrayList<Card> cards = new ArrayList<>();
    private int numTroops;

    public Player(Colour colour) {
        this.colour = colour;
        this.numTroops = 30;
    }

    public LinkedHashMap<String, Territory> getTerritories() {
        return territories;
    }

    public Colour getColour() {
        return colour;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void addTerritory(Territory territory) {
        System.out.println("Territory Name: " + territory.getName());
        territories.put(territory.getName(), territory);
        System.out.println("Gets here!");
    }

    public int getNumTroops() {
        return numTroops;
    }

    public void decrementTroops() {
        numTroops--;
    }

    public void setNumTroops(int numTroops) {
        this.numTroops = numTroops;
    }

    public void inflictCasualties(Territory territory, int numCasualties) {
        while (numCasualties > 0) {
            territories.get(territory.getName()).decrementUnits();
            numCasualties--;
        }
    }
}
