package Main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Game extends JFrame{

    private GameScreen gameScreen;
    private BufferedImage img;
    private double timePerFrame;
    private long lastFrame;

    public Game(){
    
        timePerFrame = 1000000000.0 / 60.0;

        importImg();

        setSize(640,640);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        gameScreen = new GameScreen(img);
        add(gameScreen);
        setVisible(true);

    }

    private void loopGame(){
        while(true){
            if(System.nanoTime() - lastFrame >= timePerFrame){
                lastFrame = System.nanoTime();
                repaint();
            }else{
    
            }
        }
    }

    private void importImg(){
        InputStream is = getClass().getResourceAsStream("/Main/towerDefense_tilesheet.png");
        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Game game = new Game();
        game.loopGame();
    }

}

