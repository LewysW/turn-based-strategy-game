import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Player {
    private LinkedHashMap<String, Territory> territories;
    private Colour colour;
    private ArrayList<Card> cards;

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
}
