package Main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Enemy {
    private int enemyX = 0;
    private int enemyY = 64;
    private BufferedImage image;
    private int mapX = 0, mapY = 0;
    private int health = 1;
    private int[][] enemyPathMap = {
        {0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,2,3,4,5,0,27,28,29,30,31,0,0},
        {0,0,0,0,6,0,26,0,0,0,32,0,0},
        {0,10,9,8,7,0,25,0,0,0,33,0,0},
        {0,11,0,0,0,0,24,0,0,0,34,0,0},
        {0,12,0,0,0,0,23,0,0,0,35,0,0},
        {0,13,0,0,20,21,22,0,0,0,36,0,0},
        {0,14,0,0,19,0,0,0,39,38,37,0,0},
        {0,15,16,17,18,0,0,0,40,0,0,0,0},
        {0,0,0,0,0,0,0,0,41,0,0,0,0},
        {0,49,48,47,46,45,44,43,42,0,0,0,0},
        {0,50,0,0,0,0,0,0,0,0,0,0,0},
        {0,51,52,53,54,55,56,57,58,59,60,61,62},
    };
    private double angle = 0;

    public Enemy(int enemyX, int enemyY, BufferedImage image) {
        this.enemyX = enemyX;
        this.enemyY = enemyY;
        this.image = image;
        this.mapX = enemyX / 64;
        this.mapY = enemyY / 64;
        this.health = 1;
    }

    public boolean update() {
        // Convert grid position to pixel position
        int targetX = mapX * 64;
        int targetY = mapY * 64;
    
        // Move toward the target pixel position
        if (enemyX < targetX) enemyX += 8;
        if (enemyX > targetX) enemyX -= 8;
        if (enemyY < targetY) enemyY += 8;
        if (enemyY > targetY) enemyY -= 8;

        // If reached target cell, find the next cell
        if(enemyPathMap[mapY][mapX] != 62){
            if (enemyX == targetX && enemyY == targetY) {
                findNextCell();
            }
        }else{
            return true; 
        }

        return false;
    }

    public boolean isEnemyDead(){
        if(health == 0){
            return true;
        }
        return false;
    }

    private void findNextCell() {
        // Check adjacent cells in a fixed order: right, down, left, up
        if (enemyPathMap[mapY][mapX + 1] > enemyPathMap[mapY][mapX]) {
            mapX += 1; // Move right
            angle = 0; 
        } else if (enemyPathMap[mapY + 1][mapX] > enemyPathMap[mapY][mapX]) {
            mapY += 1; // Move down
            angle = 90;
        } else if (enemyPathMap[mapY][mapX - 1] > enemyPathMap[mapY][mapX]) {
            mapX -= 1; // Move left
            angle = 180;
        } else if (enemyPathMap[mapY - 1][mapX] > enemyPathMap[mapY][mapX]) {
            mapY -= 1; // Move up
            angle = 270;
        }
    }

    public int getX() {
        return enemyX; // Replace with the actual x-coordinate field
    }
    
    public int getY() {
        return enemyY; // Replace with the actual y-coordinate field
    }

    public Rectangle getBounds() {
        return new Rectangle(enemyX, enemyY, 64, 64);
    }

    public boolean takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
        return health == 0;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        var originalTransform = g2d.getTransform();
        g2d.translate(enemyX + 32, enemyY + 32);
        g2d.rotate(Math.toRadians(angle));
        g.drawImage(image, -32, -32, null);
        g2d.setTransform(originalTransform);
    }
}
