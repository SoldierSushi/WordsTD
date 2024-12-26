package Main;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Tower {
    private BufferedImage image;
    private int towerY;
    private int towerX;
    private boolean[][] towerMap = {
        {false, false, false, false, false, false, false, false, false, false, false, false, false},
        {true, true, true, true, true, false, true, true, true, true, true, false, false},
        {false, false, false, false, true, false, true, false, false, false, true, false, false},
        {false, true, true, true, true, false, true, false, false, false, true, false, false},
        {false, true, false, false, false, false, true, false, false, false, true, false, false},
        {false, true, false, false, false, false, true, false, false, false, true, false, false},
        {false, true, false, false, true, true, true, false, false, false, true, false, false},
        {false, true, false, false, true, false, false, false, true, true, true, false, false},
        {false, true, true, true, true, false, false, false, true, false, false, false, false},
        {false, false, false, false, false, false, false, false, true, false, false, false, false},
        {false, true, true, true, true, true, true, true, true, false, false, false, false},
        {false, true, false, false, false, false, false, false, false, false, false, false, false},
        {false, true, true, true, true, true, true, true, true, true, true, true, true},
    };

    public Tower(int towerX, int towerY, BufferedImage image) {
        this.towerX = towerX;
        this.towerY = towerY;
        this.image = image;
    }

    public void draw(Graphics g) {
        g.drawImage(image, towerX*64, towerY*64, null);
    }
}