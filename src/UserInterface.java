import java.awt.*;
import java.awt.event.*;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

/**
 * Class to manage the swing user interface
 */
public class UserInterface extends JPanel {

    /**
     * Main function to run program,
     * used to set up user interface
     * @param args
     */
    public static void main(String[] args) {
        loadAssets();
        display.init();
        //TODO - in game function have initial phase
        //TODO - in game function have main game loop, terminated by a victory condition (one player has all territories or has finished missions)

    }


    //Object representing the game
    private static Game game;

    //JFrame representing the GUI window
    private JFrame frame;
    //Panel to place buttons and checkboxes on
    private JPanel panel;

    //Starts a new game
    private JButton startGame = new JButton("Start Game");

    //Allows the user to mute the music
    private JCheckBox mute = new JCheckBox();

    //Slider to allow user to adjust the audio volume
    private static JSlider audioSlider;

    private static UserInterface display = new UserInterface();
    private static UserInterface gameScreen = new UserInterface();

    private static JComboBox<String> numPlayersCombo = new JComboBox<>();
    private static JComboBox<String> gameModeCombo = new JComboBox<>();
    private static JComboBox<String> territorySelectionCombo = new JComboBox<>();

    private boolean menu = false;

    //Resolution of display
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    //Variables to store loaded images and related data
    private static ArrayList<Continent> continents = new ArrayList<>();
    private static BufferedImage background;
    private static LinkedHashMap<Colour, BufferedImage> soldiers;

    private static Music music;

    private static final String song = "resources/MUSIC/Die Walküre, WWV 86B - Fantasie.wav";

    private static void loadAssets() {
        //Loads in images and borders
        AssetLoader assetLoader = new AssetLoader();

        continents = assetLoader.loadContinents();
        background = assetLoader.loadMenuArt();
        soldiers = assetLoader.loadSoldiers();
    }


    //TODO - break down into smaller functions
    private void init() {
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

        numPlayersCombo = new JComboBox<>(numPlayersText);
        gameModeCombo = new JComboBox<>(gameModeText);
        territorySelectionCombo = new JComboBox<>(territorySelectionText);

        numPlayersCombo.setMaximumSize(new Dimension(150, 30));
        gameModeCombo.setMaximumSize(new Dimension(150, 30));
        territorySelectionCombo.setMaximumSize(new Dimension(150, 30));

        //Format buttons and elements of display:


        Box hBox1 = Box.createHorizontalBox();
        Box hBox2 = Box.createHorizontalBox();
        Box hBox3 = Box.createHorizontalBox();

        hBox1.add(numPlayersLbl);
        hBox1.add(numPlayersCombo);

        hBox2.add(gameModeLbl);
        hBox2.add(gameModeCombo);

        hBox3.add(territorySelectionLbl);
        hBox3.add(territorySelectionCombo);

        display.panel.add(hBox1);
        display.panel.add(hBox2);
        display.panel.add(hBox3);

        Box hbox4 = Box.createHorizontalBox();

        JLabel muteLbl = new JLabel("Mute audio ");

        //Adds mute label and check box to display
        hbox4.add(muteLbl);
        hbox4.add(mute);

        display.panel.add(hbox4);


        //Label for audio slider
        JLabel sliderLabel = new JLabel("Volume:");

        //Audio slider
        audioSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
        audioSlider.addChangeListener(new SliderListener());

        Box hbox5 = Box.createHorizontalBox();

        hbox.add(sliderLabel);
        hbox.add(audioSlider);

        display.panel.add(hbox5);

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

                for (Continent continent : continents) {
                    for (Territory territory : continent.getTerritories()) {
                        if (territory.getBorder().contains(new Point2D.Double(e.getX(), e.getY()))) {
                            System.out.println(territory.getName() + " has been clicked!");
                        }
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

                //Get game settings from display
                int numPlayers = Integer.parseInt((String) UserInterface.numPlayersCombo.getSelectedItem());
                String gameMode = (String) UserInterface.gameModeCombo.getSelectedItem();
                String territorySelection = (String) UserInterface.territorySelectionCombo.getSelectedItem();

                game = new Game(continents, numPlayers, gameMode, territorySelection);

                initWorldMap();

                game.run();
            }

            public void initWorldMap() {
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
            }
        });

        mute.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (mute.isSelected()) {
                    music.stopSong();
                } else {
                    music.playSong(song);
                    music.setVolume(audioSlider.getValue() / 100.0);
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
            //Fill background of display
            graphics2D.setColor(new Color(30, 140, 168));
            graphics2D.fillRect(0, 0, 1920, 1080);
            //Draws the image for each territory
            for (Continent continent : continents) {
                for (Territory territory : continent.getTerritories()) {
                    graphics2D.drawImage(territory.getImage(), 100, -25, null);
                }
            }

            //Draws a soldier icon of a particular colour in each territory occupied by the current player
            for (Player player : game.getPlayers()) {
                for (Territory territory : player.getTerritories().values()) {
                    //Gets the rectangle encompassing the territory
                    Rectangle bounds = territory.getBorder().getBounds();

                    //Gets the middle coordinate of the territory
                    double x = bounds.x + (bounds.getWidth() / 2);
                    double y = bounds.y + (bounds.getHeight() / 2) - 20;

                    System.out.println("x: " + x);
                    System.out.println("y: " + y);
                    BufferedImage soldier = soldiers.get(player.getColour());

                    //Draw soldier
                    graphics2D.drawImage(soldier, (int) x, (int) y, soldier.getWidth() / 4, soldier.getHeight() / 4, null);

                    //Draw number of units
                    graphics2D.setColor(Color.WHITE);
                    graphics2D.setFont(new Font("TimesRoman", Font.BOLD, 12));

                    double textX = (territory.getNumUnits() < 10) ? x + 5 : x;
                    double textY = y + 50;

                    graphics2D.drawString(Integer.toString(territory.getNumUnits()), (int) textX, (int) textY);
                }
            }
        }

    }

    /**
     * Listener for UI slider
     */
    static class SliderListener implements ChangeListener {
        /**
         * Run if slider's state has been changed
         * @param e - event to represent slider being adjusted
         */
        public void stateChanged(ChangeEvent e) {
            //Get value of slider
            JSlider source = (JSlider)e.getSource();
            //If slider is not currently being adjusted
            if (!source.getValueIsAdjusting()) {
                System.out.println(source.getValue());

                double gain = source.getValue() / 100.0;
                music.setVolume(gain);
            }
        }
    }

}