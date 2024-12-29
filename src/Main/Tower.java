package Main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class Tower {
    private BufferedImage image;
    private int towerY;
    private int towerX;

    public Tower(int towerX, int towerY, BufferedImage image) {
        this.towerX = towerX;
        this.towerY = towerY;
        this.image = image;
    }

    public Enemy nearestEnemy(List<Enemy> enemies) {
        Enemy nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Enemy enemy : enemies) {
            double distance = Math.sqrt(Math.pow((enemy.getY() + 32) - (towerY * 64 + 32), 2) + Math.pow((enemy.getX() + 32) - (towerX * 64 + 32), 2));
            if (distance < minDistance) {
                minDistance = distance;
                nearest = enemy;
            }
        }
        return nearest;
    }

    public double angleToNearestEnemy(Enemy enemy) {
        // Get enemy's position
        double enemyX = enemy.getX() + 32; // Adjust for enemy center if needed
        double enemyY = enemy.getY() + 32; // Adjust for enemy center if needed
    
        // Get tower's position
        double towerXd = this.towerX * 64 + 32; // Adjust for tower center
        double towerYd = this.towerY * 64 + 32; // Adjust for tower center
    
        // Calculate the angle
        double angle = Math.atan2(enemyY - towerYd, enemyX - towerXd);
    
        // Convert to degrees if needed
        double angleInDegrees = Math.toDegrees(angle);
    
        // Ensure angle is in the range [0, 360]
        if (angleInDegrees < 0) {
            angleInDegrees += 360;
        }
    
        return angleInDegrees;
    }

    public int getX() { return towerX*64; }
    
    public int getY() { return towerY*64; }

    public Projectile shoot(int speed, double angle){
        double velocityX = speed * Math.cos(angle);
        double velocityY = speed * Math.sin(angle);

        return new Projectile(towerX, towerY, velocityX, velocityY);
    }

    public void draw(Graphics g, double angleToEnemy) {
        Graphics2D g2d = (Graphics2D) g;
    
        int towerScreenX = towerX * 64;
        int towerScreenY = towerY * 64;
    
        var originalTransform = g2d.getTransform();
    
        g2d.translate(towerScreenX + 32, towerScreenY + 32);
    
        // Adjust angle by -90 degrees if the image is initially pointing up
        g2d.rotate(Math.toRadians(angleToEnemy + 90));
    
        g2d.drawImage(image, -32, -32, null);
    
        g2d.setTransform(originalTransform);
    }

    /*
     * shoot a bullet with that angle at the enemy and remove 1 health
     * if enemy health is 0, destroy enemy
     */
}