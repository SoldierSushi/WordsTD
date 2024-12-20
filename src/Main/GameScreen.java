package Main;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class GameScreen extends JPanel{ 
    private Random random;
    private BufferedImage img;
    private ArrayList <BufferedImage> sprites = new ArrayList<>()

    public GameScreen(BufferedImage img){
        this.img = img;
        random = new Random();
    }

    private void LoadSprites(){

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.drawImage(img.getSubimage(0, 32, 32, 32), 0, 0, null);
    }
    public Color randColor(){
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);

        return new Color(r,g,b);
    }
}
