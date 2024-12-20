package Main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Game extends JFrame{

    private GameScreen gameScreen;
    private BufferedImage img;

    public Game(){
    
        importImg();

        setSize(640,640);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        gameScreen = new GameScreen(img);
        add(gameScreen);
        setVisible(true);

    }

    public void importImg(){
        InputStream is = getClass().getResourceAsStream("/Main/spriteatlas.png");
        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new Game();
    }

}

