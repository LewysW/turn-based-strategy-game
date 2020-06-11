import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Player {
    private LinkedHashMap<String, Territory> territories = new LinkedHashMap<>();
    private Colour colour;
    private ArrayList<Card> cards = new ArrayList<>();

    public Player(Colour colour) {
        this.colour = colour;
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
}
