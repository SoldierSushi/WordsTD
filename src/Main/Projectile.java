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

    public void update() {
        x += velocityX;
        y += velocityY;
    }

    public boolean isOutOfBounds() {
        return x < 0 || y < 0 || x > 832 || y > 832; //change according to screen size
    }

    public boolean hasHitEnemy(List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (enemy.getBounds().intersects(getBounds())) {
                enemy.takeDamage(1);
                return true;
            }
        }
        return false;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x - 2, (int) y - 2, 4, 4); // Adjust size as needed
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect((int) x - 2, (int) y - 2, 4, 4);
    }
}
