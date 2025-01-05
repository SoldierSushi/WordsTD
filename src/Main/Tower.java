package Main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class Tower {
    private BufferedImage image;
    private int towerY;
    private int towerX;
    private double fireRate = 0.5;  // Time between shots (e.g., 1.0f = 1 second)
    private float timeSinceLastShot;

    public Tower(int towerX, int towerY, BufferedImage image, double fireRate) {
        this.towerX = towerX;
        this.towerY = towerY;
        this.image = image;
        this.fireRate = fireRate;
        this.timeSinceLastShot = 0;
    }

    public Enemy nearestEnemy(List<Enemy> enemies) {
        Enemy nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Enemy enemy : enemies) {
            double distance = Math.sqrt(Math.pow((enemy.getY() + 32) - (towerY * 64 + 32), 2) + Math.pow((enemy.getX() + 32) - (towerX * 64 + 32), 2));
            if (distance < minDistance) {
                if(distance <= 160){
                    minDistance = distance;
                    nearest = enemy;
                }
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

    public Projectile shoot(int speed, double angle, float deltaTime){ //not the most consistent method, runs from 10-15 frames
        timeSinceLastShot += deltaTime;
        if (timeSinceLastShot >= fireRate) {
            double angleInRadians = Math.toRadians(angle);
            double velocityX = speed * Math.cos(angleInRadians);
            double velocityY = speed * Math.sin(angleInRadians);

            timeSinceLastShot = 0;
            return new Projectile(towerX * 64 + 32, towerY * 64 + 32, velocityX, velocityY);
        }
        return null;
    }

    public void draw(Graphics g, double angleToEnemy) {
        Graphics2D g2d = (Graphics2D) g;
    
        int towerScreenX = towerX * 64;
        int towerScreenY = towerY * 64;
    
        var originalTransform = g2d.getTransform();
    
        g2d.translate(towerScreenX + 32, towerScreenY + 32);
    
        g2d.rotate(Math.toRadians(angleToEnemy + 90));
    
        g2d.drawImage(image, -32, -32, null);
    
        g2d.setTransform(originalTransform);
    }
}