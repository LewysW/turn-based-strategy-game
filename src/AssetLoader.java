import javax.imageio.ImageIO;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AssetLoader {
    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;


    public LinkedHashMap<String, BufferedImage> loadImages() {
        LinkedHashMap<String, BufferedImage> images = new LinkedHashMap<>();
        try {
            List<File> filesInFolder = Files.walk(Paths.get("resources/"))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            //Unzips image files
            for (File file : filesInFolder) {
                if (file.toString().endsWith(".zip")) {
                    int indexOfExt = file.toString().indexOf(".");
                    int indexOfSlash = file.toString().lastIndexOf("/");
                    String continent = file.toString().substring(indexOfSlash + 1, indexOfExt);
                    System.out.println("Continent: " + continent);
                    unzip(file.toString(), "resources/" + continent + "/");
                }
            }

            //Crawls image files
            filesInFolder = Files.walk(Paths.get("resources/"))
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
                    file.delete();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return images;
    }

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
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
