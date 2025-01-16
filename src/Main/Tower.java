package Main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class Tower {
    private BufferedImage image;
    private int towerY;
    private int towerX;
    private double fireRate;
    private float timeSinceLastShot;
    private int range;

    public Tower(int towerX, int towerY, BufferedImage image, double fireRate, int range) {
        this.towerX = towerX;
        this.towerY = towerY;
        this.image = image;
        this.fireRate = fireRate;
        this.timeSinceLastShot = 0;
        this.range = range;
    }

    /*
    Description: finds the enemy that is closest to the tower
    Pre-Condition: called by GameScreen.paintComponent(): requires List<Enemy>
    Post-Condition: returns the closest Enemy to each tower
    */
    public Enemy nearestEnemy(List<Enemy> enemies) {
        Enemy nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Enemy enemy : enemies) {
            double distance = Math.sqrt(Math.pow((enemy.getY() + 32) - (towerY * 64 + 32), 2) + Math.pow((enemy.getX() + 32) - (towerX * 64 + 32), 2));
            if (distance < minDistance && distance <= range) {
                minDistance = distance;
                nearest = enemy;
            }
        }
        return nearest;
    }

    /*
    Description: finds the angle to the closest enemy
    Pre-Condition: called if the closest enemy is found in GameScreen.paintComponent(): requires 1 Enemy
    Post-Condition: returns the double angleInDegrees
    */
    public double angleToNearestEnemy(Enemy enemy) {
        // Get enemy's position
        double enemyX = enemy.getX() + 32;
        double enemyY = enemy.getY() + 32;
    
        // Get tower's position
        double towerXd = this.towerX * 64 + 32;
        double towerYd = this.towerY * 64 + 32; 
    
        double angle = Math.atan2(enemyY - towerYd, enemyX - towerXd);
        double angleInDegrees = Math.toDegrees(angle);
    
        // Ensure angle is in the range [0, 360]
        if (angleInDegrees < 0) {
            angleInDegrees += 360;
        }
    
        return angleInDegrees;
    }

    /*
    Description: gets the x position of the attack tower
    Pre-Condition: called every time the tower's location needs to be checked
    Post-Condition: returns towerX position on the the map grid multiplied by 64 (each tile is 64*64 in game)
    */
    public int getX() { return towerX*64; }
    
    /*
    Description: gets the y position of the attack tower
    Pre-Condition: called every time the tower's location needs to be checked
    Post-Condition: returns towerY position on the the map grid multiplied by 64 (each tile is 64*64 in game)
    */
    public int getY() { return towerY*64; }

    /*
    Description: creates a new projectile to shoot out from the attack tower
    Pre-Condition: called in GameScreen.paintComponent(): requires 1 int, 1 double, 1 float
    Post-Condition: returns a new Projectile if it is off cooldown, else it returns null
    */
    public Projectile shoot(int speed, double angle, float deltaTime){
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

    /*
    Description: gets the fireRate of the attack tower
    Pre-Condition: called when upgrading the tower's fire rate
    Post-Condition: returns double fireRate
    */
    public double getFireRate(){ return fireRate; }

    /*
    Description: lowers the cooldown between a tower's fire rate
    Pre-Condition: called when upgrading the tower's fire rate: requires 1 double
    Post-Condition: lowers the attack tower's fire rate by a specified amount
    */
    public void setFireRate(double fireRateRemove) { this.fireRate -= fireRateRemove; }

    /*
    Description: gets the range of the attack tower
    Pre-Condition: called when upgrading the tower's range
    Post-Condition: returns int range
    */
    public int getRange(){ return range; }

    /*
    Description: increases the tower
    Pre-Condition: called when upgrading the tower's range
    Post-Condition: increases the attack tower's range by a specified amount
    */
    public void setRange(int rangeAdd) { this.range += rangeAdd; }

    /*
    Description: draws the attack tower
    Pre-Condition: called every time paintComponent() runs
    Post-Condition: draws an attack tower based on its coordinates and rotation 
    */
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