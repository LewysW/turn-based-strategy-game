import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class Territory {
    private String name;
    private BufferedImage image;
    private Path2D border;
    private int numUnits;

    public Territory(String name, BufferedImage image, Path2D border) {
        this.name = name;
        this.image = image;
        this.border = border;
        numUnits = 0;
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

    public void incrementUnits() {
        numUnits++;
    }

    public void decrementUnits() {
        numUnits--;
    }
}
