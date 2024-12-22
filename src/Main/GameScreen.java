package Main;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GameScreen extends JPanel implements Runnable{ 
    private BufferedImage img;
    private long fps = 0;
    private int characterX = 0;
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
        {0,1,1,1,1,1,1,1,1,1,1,1,1},
    };

    public GameScreen(BufferedImage img){
        lastTime = System.nanoTime();
        lastFrameTime = System.nanoTime();
        this.img = img;
        preloadBackground();
        fpsThreadStart();
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
        g.drawImage(img.getSubimage(20*64, 6*64, 64, 64), characterX, 64, null);

        updateShit();
    }

    public void preloadBackground(){
        backGroundImages = new Image[size][size];
        for(int y = 0; y < size; y++){
            for(int x = 0; x < size; x++){
                if(map[y][x] == 1){
                    backGroundImages[y][x] = img.getSubimage(21*64, 6*64, 64, 64);
                }else{
                    backGroundImages[y][x] = img.getSubimage(19*64, 6*64, 64, 64);
                }
            }
        }

    }

    public void fpsThreadStart(){
        lastTime = System.nanoTime();
        fpsThread = new Thread(this);
        fpsThread.start();
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
        characterX += 2;
    }
}