package Main;

import javax.swing.JPanel;
import java.awt.event.MouseEvent;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class GameScreen extends JPanel{ 
    private BufferedImage img;
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

    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Tower> towers = new ArrayList<>();

    public GameScreen(BufferedImage img) {
        this.img = img;

        preloadBackground();
        startGameThread();
        setupMouseListener();
    }

    public void startGameThread(){

        Thread gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int fps = 0;
                long spawnInterval = 1000000000; // 1 second in nanoseconds
                long lastSpawnTime = System.nanoTime();
                int enemyCounter = 0;

                while(true){
                    //Spawns enemies in intervals
                    long currentTime = System.nanoTime();
                    if (currentTime - lastSpawnTime >= spawnInterval) {
                        if(enemyCounter < 4){
                            enemies.add(new Enemy(0, 64, img.getSubimage(20 * 64, 6 * 64, 64, 64)));
                            lastSpawnTime = currentTime;
                            enemyCounter++;
                        }
                    }

                    //displays fps in console
                    if (fps == 30) {
                        System.out.println("FPS: " + fps);
                        fps = 0;
                    } else {
                        fps++;
                    }

                    //real stuff
                    updateEnemies();
                    repaint();

                    //saves cpu usage
                    long frameTime = System.nanoTime();
                    try {
                        long sleepTime = Math.max(0, 33 - (System.nanoTime() - frameTime) / 1_000_000);
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "gameThread");
        gameThread.start();
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Mouse clicked.");
                int x = e.getX() / 64;
                int y = e.getY() / 64;

                if (x >= 0 && x < size && y >= 0 && y < size) {
                    if (map[y][x] == 0) { // Only place a tower on empty tiles
                        map[y][x] = 2; // Mark the tile as occupied
                        towers.add(new Tower(x, y, img.getSubimage(19 * 64, 10 * 64, 64, 64)));
                    }
                }
            }
        });
    }

         
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        for(int y = 0; y < size; y++){
            for(int x = 0; x < size; x++){
                g.drawImage(backGroundImages[y][x], x*64, y*64, null);
            }
        }

        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }

        for(Tower tower : towers){
            tower.nearestEnemy(x, y, );
            tower.draw(g);
        }
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

    private void updateEnemies() {
        // Use an iterator to safely remove enemies
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.update()) {
                iterator.remove();
                System.out.println("Enemy reached the end and was removed.");
            }
        }
    }
}