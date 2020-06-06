import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.*;
import javax.swing.border.Border;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

/**
 * Class to manage the swing user interface
 */
public class UserInterface extends JPanel {
    //JFrame representing the GUI window
    private JFrame frame;
    //Panel to place buttons and checkboxes on
    private JPanel panel;

    //Starts a new game
    private JButton startGame = new JButton("Start Game");

    private JCheckBox mute = new JCheckBox();

    private static UserInterface display = new UserInterface();
    private static UserInterface gameScreen = new UserInterface();

    private boolean menu = false;

    //Resolution of display
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    private static LinkedHashMap<String, BufferedImage> territoryImages = new LinkedHashMap<>();
    private static LinkedHashMap<String, Path2D> borders = new LinkedHashMap<>();
    private static BufferedImage background;

    private static Music music;

    private static final String song = "resources/MUSIC/Die Walküre, WWV 86B - Fantasie.wav";



    /**
     * Main function to run program,
     * used to set up user interface
     * @param args
     */
    public static void main(String[] args) {
        display.initDisplay();
    }

    //TODO - break down into smaller functions
    private void initDisplay() {
        //Loads in images and borders
        AssetLoader assetLoader = new AssetLoader();
        territoryImages = assetLoader.loadImages();
        borders = assetLoader.loadBorders();
        background = assetLoader.loadMenuArt();


        display.frame = new JFrame();

        //Initialise panel to place buttons on
        display.panel = new JPanel();
        display.panel.setLayout(new BoxLayout(display.panel, BoxLayout.PAGE_AXIS));

        //Specifies layout of frame
        display.frame.setTitle("Where is New Zealand?!");
        display.frame.setSize(WIDTH, HEIGHT - 80);
        Container contentPane = display.frame.getContentPane();
        contentPane.add(display, BorderLayout.CENTER);

        Box hbox = Box.createHorizontalBox();
        hbox.add(display.startGame);
        //Adds start button to display
       display.panel.add(hbox);

        String[] numPlayersText = {"2", "3", "4", "5", "6"};
        String[] gameModeText = {"World Domination", "Missions"};
        String[] territorySelectionText = {"Manual", "Automatic"};

        JLabel numPlayersLbl = new JLabel("Number of Players ");
        JLabel gameModeLbl = new JLabel("Game Mode ");
        JLabel territorySelectionLbl = new JLabel("Territory Selection ");

        JComboBox<String> numPlayers = new JComboBox<>(numPlayersText);
        JComboBox<String> gameMode = new JComboBox<>(gameModeText);
        JComboBox<String> territorySelection = new JComboBox<>(territorySelectionText);

        numPlayers.setMaximumSize(new Dimension(150, 30));
        gameMode.setMaximumSize(new Dimension(150, 30));
        territorySelection.setMaximumSize(new Dimension(150, 30));

        Box hBox1 = Box.createHorizontalBox();
        Box hBox2 = Box.createHorizontalBox();
        Box hBox3 = Box.createHorizontalBox();

        hBox1.add(numPlayersLbl);
        hBox1.add(numPlayers);

        hBox2.add(gameModeLbl);
        hBox2.add(gameMode);

        hBox3.add(territorySelectionLbl);
        hBox3.add(territorySelection);

        display.panel.add(hBox1);
        display.panel.add(hBox2);
        display.panel.add(hBox3);

        Box hbox4 = Box.createHorizontalBox();

        JLabel muteLbl = new JLabel("Mute audio ");

        hbox4.add(muteLbl);
        hbox4.add(mute);

        display.panel.add(hbox4);

        //Specifies border of panel
        display.panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        //Adds panel to frame
        contentPane.add(display.panel, BorderLayout.LINE_START);
        //Sets frame to visible
        display.frame.setVisible(true);

        //Sets this window as the menu window
        display.menu = true;

        //Object to handle music in game
        music = new Music(null);
        music.playSong("resources/MUSIC/Die Walküre, WWV 86B - Fantasie.wav");

        //Draws the frame
        display.repaint();

        //Ensures that program exits when window is closed
        display.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //Sets resizable to false for the window
        display.frame.setResizable(false);
    }


    private UserInterface() {
        //Exits program on window being closed
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        //Adds points to the display when clicked
        addMouseListener(new MouseAdapter() {// provides empty implementation of all
            // MouseListener`s methods, allowing us to
            // override only those which interests us
            @Override //I override only one method for presentation
            public void mousePressed(MouseEvent e) {
                System.out.println(e.getX() + ", " + e.getY());

                for (String name : borders.keySet()) {
                    if (borders.get(name).contains(new Point2D.Double(e.getX(), e.getY()))) {
                        System.out.println(name + " has been clicked!");
                    }
                }
            }
        });

        startGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menu) {
                    display.frame.dispose();
                }

                gameScreen.frame = new JFrame();

                //Initialise panel to place buttons on
                gameScreen.panel = new JPanel();
                gameScreen.panel.setLayout(new BoxLayout(gameScreen.panel, BoxLayout.PAGE_AXIS));

                //Specifies layout of frame
                gameScreen.frame.setTitle("Where is New Zealand?!");
                gameScreen.frame.setSize(WIDTH, HEIGHT);
                Container contentPane = gameScreen.frame.getContentPane();
                contentPane.add(gameScreen, BorderLayout.CENTER);

                //Sets frame to visible
                gameScreen.frame.setVisible(true);

                //Ensures that program exits when window is closed
                gameScreen.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                //Sets resizable to false for the window
                gameScreen.frame.setResizable(false);

                //Display
                gameScreen.repaint();
            }
        });

        mute.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println("Called!");
                if (mute.isSelected()) {
                    System.out.println("Selected!");
                    music.stopSong();
                } else {
                    System.out.println("Not selected!");
                    music.playSong(song);
                }
            }
        });

    }

    /**
     * Repaints the display
     * @param g - graphics object used to paint display
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;

        if (menu) {
            graphics2D.drawImage(background, 0, 0, null);
        } else {
            for (BufferedImage img : territoryImages.values()) {
                graphics2D.drawImage(img, 100, -25, null);
            }

            graphics2D.setColor(Color.RED);
            for (Path2D path2D : borders.values()) {
                graphics2D.draw(path2D);
            }
        }

    }

}