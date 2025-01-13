package Main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JFrame{

    private BufferedImage img;
    private CardLayout cardLayout;
    private JPanel mainPanel;

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
    
        return gamePanel;
    }

    private JPanel createStartScreen() {
        JPanel startScreen = new JPanel();
        startScreen.setLayout(new BorderLayout());

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> cardLayout.show(mainPanel, "Game"));
        startScreen.add(startButton, BorderLayout.CENTER);
        return startScreen;
    }

    public static void main(String[] args){
        new Game();
    }
}