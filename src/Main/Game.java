package Main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Game extends JFrame{

    private BufferedImage img;

    public Game(){

        importImg();

        setSize(932,860);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //creating the new screens
        GameScreen gameScreen = new GameScreen(img);
        MenuScreen menuScreen = new MenuScreen();

        gameScreen.setBounds(0,0, 832, 860);
        menuScreen.setBounds(832, 0, 100, 860);

        add(gameScreen);
        add(menuScreen);

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

    public static void main(String[] args){
        new Game();
    }
}