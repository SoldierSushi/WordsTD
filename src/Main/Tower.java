package Main;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Tower {
    private BufferedImage image;
    private int towerY;
    private int towerX;

    public Tower(int towerX, int towerY, BufferedImage image) {
        this.towerX = towerX;
        this.towerY = towerY;
        this.image = image;
    }

    public void nearestEnemy(double towerY, double towerX, double enemyY, double enemyX){
        double towerDistance = Math.sqrt(Math.pow((enemyY + 32) - (towerY + 32), 2) + Math.pow(enemyX - towerX, 2));
        
    }

    public void draw(Graphics g) {
        g.drawImage(image, towerX*64, towerY*64, null);
    }

    /*
     * find the distance between tower and all enemies using the hypotenus
     * target the closest enemy
     * find the angle from tower to enemy
     * rotate the tower to that degree
     * shoot a bullet with that angle at the enemy and remove 1 health
     * if enemy health is 0, destroy enemy
     */
}