package Main;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GameScreen extends JPanel implements Runnable{ 
    private BufferedImage img;
    private long fps = 0;
    private int enemyX = 0;
    private int enemyY = 64;
    private int enemyCurrentPos = 1;
    private long lastTime;
    private long lastFrameTime;
    Thread fpsThread;
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
        {0,51,52,53,54,55,56,57,58,59,60,61},
    };
    private int mapX = 0, mapY = 0;

    public GameScreen(BufferedImage img) {
        lastTime = System.nanoTime();
        lastFrameTime = System.nanoTime();
        this.img = img;
        preloadBackground();
    
        // Find starting position in the map
        for (int y = 0; y <= size; y++) {
            for (int x = 0; x < size; x++) {
                if (map[y][x] == 1) {
                    mapX = x;
                    mapY = y;
                    enemyX = mapX * 64;
                    enemyY = mapY * 64;
                    break;
                }
            }
            if (map[mapY][mapX] == 1) break;
        }
    
        fpsThreadStart();
    }

    @Override
    public void run() {
        while (true) {
            long currentTime = System.nanoTime(); //finds the last time the frame needs to be changed
            if (System.nanoTime() - lastTime >= timePerFrame) {
                updateEnemy(); // Update logic
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

        //draw characters
        g.drawImage(img.getSubimage(20*64, 6*64, 64, 64), enemyX, enemyY, null);

        updateShit();
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

    public void fpsThreadStart(){
        lastTime = System.nanoTime();
        fpsThread = new Thread(this);
        fpsThread.start();
    }

    private void updateShit() {
        fps++;
        long currentFrameTime = System.nanoTime();
        if (currentFrameTime - lastFrameTime >= 1000000000) { // One second has passed
            System.out.println("FPS: " + fps);
            fps = 0;
            lastFrameTime = currentFrameTime;
        }
    }

    private void updateEnemy() {
        // Convert grid position to pixel position
        int targetX = mapX * 64;
        int targetY = mapY * 64;
    
        // Move toward the target pixel position
        if (enemyX < targetX) enemyX += 8;
        if (enemyX > targetX) enemyX -= 8;
        if (enemyY < targetY) enemyY += 8;
        if (enemyY > targetY) enemyY -= 8;
    
        // If reached target cell, find the next cell
        if(enemyPathMap[mapY][mapX] != 61){
            if (enemyX == targetX && enemyY == targetY) {
                findNextCell();
            }
        }
    }

    private void findNextCell() {
        // Check adjacent cells in a fixed order: right, down, left, up
        if (enemyPathMap[mapY][mapX + 1] > enemyPathMap[mapY][mapX]) {
            mapX += 1; // Move right
        } else if (enemyPathMap[mapY + 1][mapX] > enemyPathMap[mapY][mapX]) {
            mapY += 1; // Move down
        } else if (enemyPathMap[mapY][mapX - 1] > enemyPathMap[mapY][mapX]) {
            mapX -= 1; // Move left
        } else if (enemyPathMap[mapY - 1][mapX] > enemyPathMap[mapY][mapX]) {
            mapY -= 1; // Move up
        }
    }
}