import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.LinkedHashSet;

public class Territory {
    private String name;
    private BufferedImage image;
    private Path2D border;
    private int numUnits;
    private Point2D troopCoord;
    private LinkedHashSet<String> adjacent;

    public Territory(String name, BufferedImage image, Path2D border, Point2D troopCoord, LinkedHashSet<String> adjacent) {
        this.name = name;
        this.image = image;
        this.border = border;
        this.numUnits = 0;
        this.troopCoord = troopCoord;
        this.adjacent = adjacent;
    }

    public String getName() {
        return name;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Path2D getBorder() {
        return border;
    }

    public int getNumUnits() {
        return numUnits;
    }

    public void setNumUnits(int numUnits) {
        this.numUnits = numUnits;
    }

    public Point2D getTroopCoord() {
        return troopCoord;
    }

    public LinkedHashSet<String> getAdjacent() {return adjacent;}

    public void incrementUnits() {
        numUnits++;
    }

    public void decrementUnits() {
        numUnits--;
    }
}
