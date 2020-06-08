import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Continent {
    private String name;
    private ArrayList<Territory> territories;
    private int bonus;

    public Continent(String name, ArrayList<Territory> territories, int bonus) {
        this.name = name;
        this.territories = territories;
        this.bonus = bonus;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Territory> getTerritories() {
        return territories;
    }

    public int getBonus() {
        return bonus;
    }
}
