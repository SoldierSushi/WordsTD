package Main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

public class Projectile {
    private double x, y, velocityX, velocityY;

    public Projectile(double x, double y, double velocityX, double velocityY) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    /*
    Description: makes the projectile move
    Pre-Condition: called every time paintComponent() runs
    Post-Condition: moves projectile by their velocityX and velocityY values
    */
    public void update() {
        x += velocityX;
        y += velocityY;
    }

    /*
    Description: checks if the enemy is out of bounds
    Pre-Condition: called alongside update()
    Post-Condition: returns a boolean true if out of bounds, returns false if not
    */
    public boolean isOutOfBounds() {
        return x < 0 || y < 0 || x > 832 || y > 832;
    }

    /*
    Description: checks if the projectile has hit an enemy
    Pre-Condition: called alongside update(): requires List<Enemy>
    Post-Condition: checks if the enemy's hitbox intersects with the projectiles hitbox, then returns boolean
    */
    public boolean hasHitEnemy(List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (enemy.getBounds().intersects(getBounds())) {
                enemy.takeDamage(1);
                return true;
            }
        }
        return false;
    }

    /*
    Description: creates the boundaries for the bullet
    Pre-Condition: called by hasHitEnemy()
    Post-Condition: returns a Rectangle the size of the bullet
    */
    public Rectangle getBounds() {
        return new Rectangle((int) x - 2, (int) y - 2, 8, 8);
    }

    /*
    Description: draws the projectile
    Pre-Condition: called every time paintComponent() runs
    Post-Condition: draws a red rectangle based on the tower's position
    */
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect((int) x - 2, (int) y - 2, 8, 8);
    }
}