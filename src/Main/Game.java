package Main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
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

        setVisible(true);
    }

    private void importImg(){
        InputStream is = getClass().getResourceAsStream("/Main/towerDefense_tilesheet.png");
        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public static void main(String[] args){
        new Game();
    }
}