package Main;

import javax.swing.JOptionPane;
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

    private static ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Tower> towers = new ArrayList<>();
    private ArrayList<EnergyTower> energyTowers = new ArrayList<>();
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private int fps = 0;
    private long lastUpdateTime;
    private long currentTime;
    private long lastUpdateTimeEnergy;
    private long currentTimeEnergy;
    private static int wave = 0;
    private int userHP = 3;

    public GameScreen(BufferedImage img, MenuScreen menuScreen) {
        this.img = img;

        preloadBackground();
        startGameThread();
        setupMouseListener();

        setBounds(0,0, 832, 860);
        lastUpdateTime = System.nanoTime();
        
        menuScreen.setWordCompletedCallback(() -> damageFirstEnemy(menuScreen.getWordTyped().length()));
        menuScreen.startGameCallback(this::startGameThread);
    }

    public void startGameThread(){

        Thread gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Game thread started. Game state: " + MenuScreen.isGameTrue());
                fps = 0;
                long spawnInterval = 1000000000; // 1 second in nanoseconds
                long lastSpawnTime = System.nanoTime();
                int enemyCounter = 0;

                while(MenuScreen.isGameTrue()){
                    //Spawns enemies in intervals
                    long currentSpawnTime = System.nanoTime();
                    if (currentSpawnTime - lastSpawnTime >= spawnInterval) {
                        if(enemyCounter < 4){
                            enemies.add(new Enemy(0, 64, img.getSubimage(20 * 64, 6 * 64, 64, 64)));
                            lastSpawnTime = currentSpawnTime;
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
                System.out.println("mouse clicked");
                if(MenuScreen.isTowerAttackOn()){
                    int x = e.getX() / 64;
                    int y = e.getY() / 64;

                    if (x >= 0 && x < size && y >= 0 && y < size) {
                        if (map[y][x] == 0) { // Only place a tower on empty tiles
                            map[y][x] = 2; // Mark the tile as occupied
                            towers.add(new Tower(x, y, img.getSubimage(19 * 64, 10 * 64, 64, 64), 0.2));
                        }
                    }
                    MenuScreen.flipTowerAttackValue();

                }else if(MenuScreen.isEnergyTowerOn()){
                    int x = e.getX() / 64;
                    int y = e.getY() / 64;

                    if (x >= 0 && x < size && y >= 0 && y < size) {
                        if (map[y][x] == 0) {
                            map[y][x] = 3;
                            energyTowers.add(new EnergyTower(x, y, img.getSubimage(20 * 64, 10 * 64, 64, 64), 1));
                        }
                    }
                    MenuScreen.flipEnergyTowerValue();
                }
            }
        });
    }

         
    @Override
    public void paintComponent(Graphics g){
        double angleToEnemy = 0;
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

            Enemy nearestEnemy = tower.nearestEnemy(enemies);
            angleToEnemy = 0;

            currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0f;  // Convert to seconds

            if (nearestEnemy != null) { // posibly move this to Tower class
                angleToEnemy = tower.angleToNearestEnemy(nearestEnemy);
                Projectile newProjectile = tower.shoot(40, angleToEnemy, deltaTime);
                if (newProjectile != null) {
                    projectiles.add(newProjectile);
                }
            }
            
            tower.draw(g, angleToEnemy);
        }
        lastUpdateTime = currentTime;

        for(EnergyTower energyTower : energyTowers){
            currentTimeEnergy = System.nanoTime();
            float deltaTimeEnergy = (currentTimeEnergy - lastUpdateTimeEnergy) / 1_000_000_000.0f;  // Convert to seconds
            energyTower.harvestEnergy(deltaTimeEnergy);
            energyTower.draw(g);
        }
        lastUpdateTimeEnergy = currentTimeEnergy;

        if (projectiles != null) {
            updateProjectiles(g);
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
                userLoseHP();
                iterator.remove();
                System.out.println("Enemy reached the end");
            }else if(enemy.isEnemyDead()){
                iterator.remove();
                System.out.println("Enemy killed");
            }
            checkWaveComplete();
        }
    }

    public void damageFirstEnemy(int damage) {
        if (!enemies.isEmpty()) {
            Enemy firstEnemy = enemies.get(0);
            if (firstEnemy.takeDamage(damage)) {
                enemies.remove(firstEnemy); // Remove if health is 0
                System.out.println("Enemy with text!");
            }
        }
    }

    private void updateProjectiles(Graphics g){
        Iterator<Projectile> projectileIterator = projectiles.iterator();
        while (projectileIterator.hasNext()) {
            Projectile projectile = projectileIterator.next();
            projectile.update();
            if (projectile.isOutOfBounds() || projectile.hasHitEnemy(enemies)) {
                projectileIterator.remove();
            } else {
                projectile.draw(g);
            }
        }
    }

    public static void checkWaveComplete(){
        if(enemies.isEmpty()){
            System.out.println("wave " + (wave + 1) + " complete");
            wave++;
            MenuScreen.flipPlayOn();
            MenuScreen.resetPlayButton();
        }
    }

    public void userLoseHP(){
        userHP--;
        if(userHP <= 0){
            JOptionPane.showMessageDialog(null, "You lost, better luck next time!", "You Lose", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
    

}