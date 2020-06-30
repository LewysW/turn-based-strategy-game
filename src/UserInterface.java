import java.awt.*;
import java.awt.event.*;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Random;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;
import static java.lang.Thread.sleep;

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
    private static LinkedHashMap<Colour, ArrayList<BufferedImage>> dice;
    private static BufferedImage swords;

    private static Music music;

    private static final String song = "resources/MUSIC/Die Walküre, WWV 86B - Fantasie.wav";

    private static final Point2D[] redDiceCoords = {new Point2D.Double(25, 510),
            new Point2D.Double(125, 510), new Point2D.Double(225, 510)};
    private static final Point2D[] whiteDiceCoords = {new Point2D.Double(70, 620), new Point2D.Double(170, 620)};

    private static Rectangle turnButton = new Rectangle(70, 20, 120, 80);

    private static void loadAssets() {
        //Loads in images and borders
        AssetLoader assetLoader = new AssetLoader();

        continents = assetLoader.loadContinents();
        background = assetLoader.loadMenuArt();
        soldiers = assetLoader.loadSoldiers();
        dice = assetLoader.loadDice();
        swords = assetLoader.loadAttackingImages();
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
                //Midpoint of display
                double x = (gameScreen.getLocationOnScreen().getX() + gameScreen.getWidth()) / 2;
                double y = (gameScreen.getLocationOnScreen().getY() + gameScreen.getHeight()) / 2;
                Point2D midpoint = new Point2D.Double(x, y);

                //Coordinate clicked
                Point2D coordinate = new Point2D.Double(e.getX(), e.getY());

                //Use point differently depending on the state of the game
                switch (game.getState()) {
                    case TERRITORY_SELECTION:
                    case TROOP_DEPLOYMENT:
                    case REINFORCEMENTS:
                        boolean deployed = game.deployUnit(coordinate);

                        if (deployed) {
                            repaint();
                        }
                        break;
                    case ATTACK_PHASE:
                        boolean launchingAttack = game.attack(coordinate);

                        //If attack is being launched
                        if (launchingAttack) {
                            //Animate the dice rolling
                            animateDice();
                            //Launch attack from first to second country
                            game.launchAttack();

                            //Update territories
                            if (game.getAttackPhase().getDefending().getNumUnits() == 0) {
                                repaint();

                                System.out.println("(x,y) - " + "(" + x + "," + y + ")");
                                game.transferTerritory(midpoint);
                                game.resetAttackPhase();
                            }
                        }

                        //Update display
                        repaint();
                        break;
                    case TACTICAL_MOVE_PHASE:
                        if (game.tacticalMovePhase(coordinate)) {
                                repaint();
                                game.transferTroops(midpoint);
                        }

                        //Update display
                        repaint();
                        break;

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

    //TODO - break down to smaller functions
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
            //TODO - move to function
            //Fill background of display
            graphics2D.setColor(new Color(30, 140, 168));
            graphics2D.fillRect(0, 0, 1920, 1080);

            graphics2D.setColor(Color.DARK_GRAY);
            graphics2D.fillOval(0, 0, 53, 106);

            graphics2D.setColor(Color.lightGray);
            graphics2D.fillOval(3, 3, 47, 100);

            //Display current player
            BufferedImage currentPlayer = soldiers.get(game.getPlayers().get(game.getTurn()).getColour());
            graphics2D.drawImage(currentPlayer, 17, 10, currentPlayer.getWidth() / 4, currentPlayer.getHeight() / 4, null);

            //TODO - move to function
            //Draw troop icon in top left of display
            switch (game.getState()) {
                case TROOP_DEPLOYMENT:
                case REINFORCEMENTS:
                    //Draw number of units remaining for current player
                    graphics2D.setColor(Color.WHITE);
                    graphics2D.setFont(new Font("TimesRoman", Font.BOLD, 12));

                    int numUnits = game.getPlayers().get(game.getTurn()).getNumTroops();
                    int x = (numUnits < 10) ? 22 : 18;

                    graphics2D.drawString(Integer.toString(numUnits), x, 60);
            }

            //Draws the image for each territory
            for (Continent continent : continents) {
                for (Territory territory : continent.getTerritories()) {
                    graphics2D.drawImage(territory.getImage(), 100, -25, null);
                }
            }

            //TODO - move to function
            //Draws a soldier icon of a particular colour in each territory occupied by the current player
            for (Player player : game.getPlayers()) {
                for (Territory territory : player.getTerritories().values()) {
                    BufferedImage soldier = soldiers.get(player.getColour());

                    double x = territory.getTroopCoord().getX() - 10;
                    double y = territory.getTroopCoord().getY() - 40;

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

            //Display button to end attack or tactical move phase
            switch (game.getState()) {
                case ATTACK_PHASE:
                case TACTICAL_MOVE_PHASE:
                    //Display button
                    graphics2D.setColor(Color.BLACK);
                    graphics2D.setStroke(new BasicStroke(3));
                    graphics2D.draw(turnButton);
                    graphics2D.setColor(new Color(46, 184, 46));
                    graphics2D.fill(turnButton);
                    graphics2D.setColor(Color.black);

                    if (game.getState() == State.ATTACK_PHASE) {
                        graphics2D.drawString("End Attack", (int) turnButton.getCenterX() - 35, (int) turnButton.getCenterY() - 5);
                        graphics2D.drawString("Phase", (int) turnButton.getCenterX() - 20, (int) turnButton.getCenterY() + 10);
                    } else {
                        graphics2D.drawString("End Turn", (int) turnButton.getCenterX() - 27, (int) turnButton.getCenterY() + 5);
                    }
                    break;
            }

            //TODO - move to function
            //Draw outline of attacking and defending countries if selected during attack phase
            if (game.getState() == State.ATTACK_PHASE) {
                AttackStage stage = game.getAttackPhase().getStage();

                switch (stage) {
                    case DEFENDER_SELECTED:
                    case TWO_DICE:
                    case THREE_DICE:
                        graphics2D.setColor(Color.RED);
                        graphics2D.draw(game.getAttackPhase().getDefending().getBorder());

                        //Draw board with one dice
                        drawBoard(g, graphics2D);

                        //Draw first red dice
                        drawDice(graphics2D, Colour.RED, game.getAttackingDice().getDice().get(0), 1);
                        //Draw first white dice
                        drawDice(graphics2D, Colour.WHITE, game.getDefendingDice().getDice().get(0), 1);

                        //If two dice or three dice selected
                        if (stage.equals(AttackStage.TWO_DICE) || stage.equals(AttackStage.THREE_DICE)) {
                            //Draw second red dice
                            drawDice(graphics2D, Colour.RED, game.getAttackingDice().getDice().get(1), 2);

                            //If three dice selected
                            if (stage.equals(AttackStage.THREE_DICE)) {
                                //Draw third red dice
                                drawDice(graphics2D, Colour.RED, game.getAttackingDice().getDice().get(2), 3);
                            }

                            //Draw second white dice
                            if (game.getAttackPhase().getWhiteDice() == 2) {
                                drawDice(graphics2D, Colour.WHITE, game.getDefendingDice().getDice().get(1), 2);
                            }
                        }
                    case ATTACKER_SELECTED:
                        graphics2D.setColor(Color.BLUE);
                        graphics2D.draw(game.getAttackPhase().getAttacking().getBorder());
                        break;
                }
            } else if (game.getState() == State.TACTICAL_MOVE_PHASE) {
                MoveStage stage = game.getTacticalMovePhase().getStage();

                switch (stage) {
                    //Draw border of destination territory if selected
                    case DESTINATION_SELECTED:
                        graphics2D.setColor(Color.GREEN);
                        graphics2D.draw(game.getTacticalMovePhase().getDestination().getBorder());
                    //Draw border of source territory if selected
                    case SOURCE_SELECTED:
                        graphics2D.setColor(Color.BLUE);
                        graphics2D.draw(game.getTacticalMovePhase().getSource().getBorder());
                        break;
                }
            }
        }

    }

    public void animateDice() {
        for (int roll = 0; roll < 5; roll++) {
            game.rollDice();
            paintImmediately(0,0,1920, 1080);
            try {
                sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Draw a dice to the screen
     * @param graphics2D - to draw dice
     * @param colour - of dice
     * @param num - of dice
     */
    public void drawDice(Graphics2D graphics2D, Colour colour, int num, int dicePos) {
        if (colour.equals(Colour.RED)) {
            graphics2D.drawImage(dice.get(colour).get(num - 1), (int) redDiceCoords[dicePos - 1].getX(), (int) redDiceCoords[dicePos - 1].getY(), null);
        } else {
            graphics2D.drawImage(dice.get(colour).get(num - 1), (int) whiteDiceCoords[dicePos - 1].getX(), (int) whiteDiceCoords[dicePos - 1].getY(), null);
        }
    }

    public void drawBoard(Graphics g, Graphics2D graphics2D) {
        graphics2D.setColor(new Color(164, 96, 28));
        graphics2D.fillRect( 25, 500, 300, 225);
        int redDice = game.getAttackPhase().getRedDice();
        int whiteDice = game.getAttackPhase().getWhiteDice();


        if (redDice < 2) {
            drawDottedSquare(g, 135, 520, 80, 80, Color.RED);
        }

        if (redDice < 3) {
            drawDottedSquare(g, 230, 520, 80, 80, Color.RED);
        }

        if (whiteDice < 2) {
            drawDottedSquare(g, 175, 630, 80, 80, Color.WHITE);
        }

        //TODO - move board code into class
        graphics2D.setColor(Color.gray);
        graphics2D.fillRect(123, 738, 95, 95);
        graphics2D.setColor(new Color(143, 26, 13));
        graphics2D.fillRect(125, 740, 90, 90);
        graphics2D.drawImage(swords, 125, 745, swords.getWidth() / 15, swords.getHeight() / 15, null);
        graphics2D.setColor(new Color(252, 219, 28));
        graphics2D.drawString("ATTACK!", 140, 750);
    }

    public void drawDottedSquare(Graphics g, int x, int y, int w, int h, Color c) {
        //creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();

        //Sets the colour
        g2d.setColor(c);

        //set the stroke of the copy, not the original
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(dashed);
        g2d.drawRect(x, y, w, h);

        //gets rid of the copy
        g2d.dispose();
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
                double gain = source.getValue() / 100.0;
                music.setVolume(gain);
            }
        }
    }

}