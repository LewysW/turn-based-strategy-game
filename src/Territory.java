import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class Territory {
    private String name;
    private BufferedImage image;
    private Path2D border;
    private int numUnits;
    private Point2D troopCoord;

    public Territory(String name, BufferedImage image, Path2D border, Point2D troopCoord) {
        this.name = name;
        this.image = image;
        this.border = border;
        this.numUnits = 0;
        this.troopCoord = troopCoord;
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

    public Point2D getTroopCoord() {
        return troopCoord;
    }

    public void incrementUnits() {
        numUnits++;
    }

    public void decrementUnits() {
        numUnits--;
    }
}
