import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Dice {
    ArrayList<Integer> die;

    public Dice() {
        die = new ArrayList<>();
        init();
    }

    public void init() {
        die.clear();
        die.add(1);
        die.add(2);
        die.add(3);
    }

    public void roll(int num) {
        Random rand = new Random();
        die.clear();

        while (num > 0) {
            die.add(rand.nextInt(6) + 1);
            num--;
        }

        Collections.sort(die);
    }

    public ArrayList<Integer> getDice() {
        return die;
    }

    public static ArrayList<Integer> compare(Dice attacking, Dice defending) {
        ArrayList<Integer> casualties = new ArrayList<>();
        //Initial attacking and defending casualties
        casualties.add(0);
        casualties.add(0);


        int a = attacking.getDice().size() - 1;
        int d = defending.getDice().size() - 1;

        //Remove soldier from losing territory
        while (d >= 0) {
            //If attacker wins, remove one soldier from defending territory
            if (attacking.getDice().get(a) > defending.getDice().get(d)) {
                casualties.set(1, casualties.get(1) + 1);
                //If defender wins, remove one soldier from attacking territory
            } else {
                casualties.set(0, casualties.get(0) + 1);
            }

            a--;
            d--;

            //TODO - use dice object values to update dice pictures on board
            //TODO - update state after attack
        }

        return casualties;
    }
}
