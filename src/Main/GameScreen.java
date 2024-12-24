package Main;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class GameScreen extends JPanel implements Runnable{ 
    private BufferedImage img;
    private long fps = 0;
    private long lastTime;
    private long lastFrameTime;
    private double timePerFrame = 1000000000.0 / 30.0;
    private Image[][] backGroundImages;
    private int size = 13;
    private int[][] map = {
        {0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,1,1,1,1,0,1,1,1,1,1,0,0},
        {0,0,0,0,1,0,1,0,0,0,1,0,0},
        {0,1,1,1,1,0,1,0,0,0,1,0,0},
        {0,1,0,0,0,0,1,0,0,0,1,0,0},
        {0,1,0,0,0,0,1,0,0,0,1,0,0},
        {0,1,0,0,1,1,1,0,0,0,1,0,0},
        {0,1,0,0,1,0,0,0,1,1,1,0,0},
        {0,1,1,1,1,0,0,0,1,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,0,0,0,0},
        {0,1,1,1,1,1,1,1,1,0,0,0,0},
        {0,1,0,0,0,0,0,0,0,0,0,0,0},
        {0,1,1,1,1,1,1,1,1,1,1,1,9},
    };

    private ArrayList<Enemy> enemies;

    public GameScreen(BufferedImage img) {
        lastTime = System.nanoTime();
        lastFrameTime = System.nanoTime();
        this.img = img;
        preloadBackground();
        Thread fpsThread = new Thread(this);
        fpsThread.start();
    }

    @Override
    public void run() {
        // Initialize enemies
        enemies = new ArrayList<>();
        long spawnInterval = 1000000000; // 1 second in nanoseconds
        long lastSpawnTime = System.nanoTime();
        int enemyCounter = 0;

        while (true) {
            long currentTime = System.nanoTime(); //finds the last time the frame needs to be changed

            if (currentTime - lastSpawnTime >= spawnInterval) {
                if(enemyCounter < 4){
                    enemies.add(new Enemy(0, 64, img.getSubimage(20 * 64, 6 * 64, 64, 64)));
                    lastSpawnTime = currentTime; // Reset spawn timer
                    enemyCounter++;
                }
            }

            if (System.nanoTime() - lastTime >= timePerFrame) {
                updateEnemies(); // Update logic
                repaint(); // Request a repaint
                lastTime = currentTime; // Reset frame timer
            }
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        for(int y = 0; y < size; y++){
            for(int x = 0; x < size; x++){
                g.drawImage(backGroundImages[y][x], x*64, y*64, null);
            }
        }

        // Draw all enemies
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }

        updateFPS();
    }

    public void preloadBackground(){
        backGroundImages = new Image[size][size];
        for(int y = 0; y < size; y++){
            for(int x = 0; x < size; x++){
                if(map[y][x] == 0){
                    backGroundImages[y][x] = img.getSubimage(19*64, 6*64, 64, 64);
                }else if(map[y][x] == 1){
                    backGroundImages[y][x] = img.getSubimage(21*64, 6*64, 64, 64);
                }else if(map[y][x] == 9){
                    backGroundImages[y][x] = img.getSubimage(22*64, 4*64, 64, 64);
                }
            }
        }

    }

    private void updateFPS() {
        fps++;
        long currentFrameTime = System.nanoTime();
        if (currentFrameTime - lastFrameTime >= 1000000000) { // One second has passed
            System.out.println("FPS: " + fps);
            fps = 0;
            lastFrameTime = currentFrameTime;
        }
    }

    private void updateEnemies() {
        // Use an iterator to safely remove enemies
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.update()) {
                // Remove enemy if it reached the end goal
                iterator.remove();
                System.out.println("Enemy reached the end and was removed.");
            }
        }
    }
}