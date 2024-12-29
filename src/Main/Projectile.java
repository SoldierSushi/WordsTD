package Main;

import java.awt.Graphics;

public class Projectile {
    private int x, y;
    private double velocityX, velocityY;

    public Projectile(int x, int y, double velocityX, double velocityY) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void update() {
        x += velocityX;
        y += velocityY;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
        update();
    }

}
