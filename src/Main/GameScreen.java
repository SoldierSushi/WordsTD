package Main;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class GameScreen extends JPanel{ 
    private Random random;
    private BufferedImage img;
    private ArrayList <BufferedImage> sprites = new ArrayList<>();
    private long lastTime = System.currentTimeMillis();
    private int frames;

    public GameScreen(BufferedImage img){
        this.img = img;
        LoadSprites();
        random = new Random();

    }

    private void LoadSprites(){
        for(int y = 0; y < 10; y++){
            for(int x = 0; x < 10; x++){
                sprites.add(img.getSubimage(20*64, 10*64, 64, 64));
            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        for(int y = 0; y < 10; y++){
            for(int x = 0; x < 10; x++){
                g.drawImage(img.getSubimage(19*64, 6*64, 64, 64), x*64, y*64, null);
            }
        }

        callFPS();
    }

    private void callFPS(){
        frames++;
        if(System.currentTimeMillis() - lastTime >= 1000){
            System.out.println("FPS: " + frames);
            frames = 0;
            lastTime = System.currentTimeMillis();
        }
    }
}
