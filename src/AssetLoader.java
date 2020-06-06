import javax.imageio.ImageIO;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AssetLoader {
    public LinkedHashMap<String, BufferedImage> loadImages() {
        LinkedHashMap<String, BufferedImage> images = new LinkedHashMap<>();
        try {
            List<File> filesInFolder = Files.walk(Paths.get("resources/"))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            for (File file : filesInFolder) {
                if (file.toString().endsWith(".png")) {
                    //Gets name of file before '.png'
                    int indexOfExt = file.toString().indexOf(".");
                    int indexOfSlash = file.toString().indexOf("/");
                    String territoryName = file.toString().substring(indexOfSlash + 1, indexOfExt);
                    System.out.println("Name: " + territoryName);
                    images.put(territoryName, ImageIO.read(file));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return images;
    }

    public LinkedHashMap<String, Path2D> loadBorders() {
        LinkedHashMap<String, Path2D> borders = new LinkedHashMap<>();
        try {
            List<File> filesInFolder = Files.walk(Paths.get("resources/"))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            for (File file : filesInFolder) {
                if (file.toString().endsWith(".txt")) {
                    //Gets name of file before '.txt'
                    int indexOfExt = file.toString().indexOf(".");
                    int indexOfSlash = file.toString().indexOf("/");
                    String territoryName = file.toString().substring(indexOfSlash + 1, indexOfExt);

                    Path2D path = new Path2D.Double();

                    BufferedReader br = Files.newBufferedReader(file.toPath());
                    String line = br.readLine();
                    double startX = Double.parseDouble(line.split(",")[0]);
                    double startY = Double.parseDouble(line.split(",")[1]);
                    path.moveTo(startX, startY);

                    while ((line = br.readLine()) != null) {
                        String[] coords = line.split(",");
                        System.out.println("Coord: " + coords[0] + "," + coords[1]);
                        path.lineTo(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
                    }

                    path.lineTo(startX, startY);
                    borders.put(territoryName, path);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return borders;
    }

    public BufferedImage loadMenuArt() {
        try {
            return ImageIO.read(new File("resources/MENU/ChargeOfTheLightBrigade.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
