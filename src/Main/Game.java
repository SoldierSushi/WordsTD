package Main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Game extends JFrame{

    private BufferedImage img;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JLabel titleLabel;
    private Clip clip;

    public Game(){

        importImg();

        setSize(1032,860);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel titlePanel = createStartScreen();
        JPanel gamePanel = createGameScreen();

        mainPanel.add(titlePanel, "TitleScreen");
        mainPanel.add(gamePanel, "Game");

        add(mainPanel);

        playMusic("src/Main/converted_youtube_edwooGpMg8g_audio.wav");

        setVisible(true);
    }

    /*
    Description: imports png image to use for sprites
    Pre-Condition: Game class needs to be run
    Post-Condition: sets BufferedImage img to the png
     */
    private void importImg(){
        InputStream is = getClass().getResourceAsStream("/Main/towerDefense_tilesheet.png");
        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Description: creates the game screen where the user plays the game
    Pre-Condition: Game class needs to be run
    Post-Condition: returns the values in gamePanel and sets them to the actual gamePanel
     */  
    private JPanel createGameScreen() {
        JPanel gamePanel = new JPanel(null);
    
        MenuScreen menuScreen = new MenuScreen();
        GameScreen gameScreen = new GameScreen(img, menuScreen);
    
        menuScreen.setBounds(832, 0, 200, 860);
        gameScreen.setBounds(0, 0, 832, 860); 
    
        gamePanel.add(menuScreen);
        gamePanel.add(gameScreen);

        gamePanel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & e.SHOWING_CHANGED) != 0 && gamePanel.isShowing()) {
                menuScreen.requestFocusInWindow();
            }
        });
    
        return gamePanel;
    }

    /*
    Description: creates the game screen where the user plays the game
    Pre-Condition: Game class needs to be run
    Post-Condition: returns the values in gamePanel and sets them to the actual gamePanel
     */
    private JPanel createStartScreen() {
        JPanel startScreen = new JPanel(null);

        titleLabel = new JLabel("WORDS TD");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 300, 1032, 100);
        titleLabel.setForeground(Color.BLACK); // Set text color
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 64));

        JButton startButton = new JButton("Start");
        startButton.setBounds(316, 400, 400, 100);
        startButton.addActionListener(e -> cardLayout.show(mainPanel, "Game"));

        startScreen.add(startButton);
        startScreen.add(titleLabel);
        return startScreen;
    }

    /*
    Description: plays audio based on the wav. file provided
    Pre-Condition: Game class needs to be run
    Post-Condition: plays audioFile and loops while playing
     */
    private void playMusic(String filePath){
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /*
    Description: runs the constructor for Game class
    Pre-Condition: None
    Post-Condition: creates a new instance of Game constructor
     */
    public static void main(String[] args){
        new Game();
    }
}