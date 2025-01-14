package Main;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

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
    private static int wave = 19;
    private static int userHP = 3;
    private static int enemyCounter = 0;
    private long lastSpawnTime = 0;
    private static boolean allEnemiesMade = false;
    private int hoveredTileX = -1;
    private int hoveredTileY = -1;
    private double fireRate = 1;
    private int fireRateCost = 15;
    private int amountOfEnemies = 0;
    private int range = 160;
    private int rangeCost = 4;
    private static int money = 1000;
    private static int towerCost = 20;
    private static int energyTowerCost = 100;
    private Tower hoveredTower = null;
    private JMenuItem fireRateOption;
    private JMenuItem rangeOption;
    
        public GameScreen(BufferedImage img, MenuScreen menuScreen) {
            this.img = img;
    
            preloadBackground();
            startTowerPlacingThread();
            startGameThread();
            setupMouseListener();
            setupMouseMotionListener();
    
            setBounds(0,0, 832, 860);
            lastUpdateTime = System.nanoTime();
            
            menuScreen.setWordCompletedCallback(() -> damageFirstEnemy(menuScreen.getWordTyped().length()));
            menuScreen.startGameCallback(this::startGameThread);
        }
    
        public void startGameThread(){
            Thread gameThread = new Thread(new Runnable() {
                @Override
                public void run() { 
                    fps = 0;
                    double spawnSpeed = 1.6 * Math.pow(0.9, wave);
                    amountOfEnemies += 5 * (int) Math.pow(1.1, wave);
                    System.out.println("Spawn Speed: " + spawnSpeed);
                    System.out.println("Enemies to spawn: " + amountOfEnemies);
                    float spawnInterval = 1000000000 * (float) spawnSpeed ; // 1 second in nanoseconds
                    lastSpawnTime = System.nanoTime();
                    while(MenuScreen.isGameTrue()){
                        makeEnemies(spawnInterval);
        
                        //displays fps in console
                        if (fps == 30) {
                            System.out.println("FPS: " + fps);
                            fps = 0;
                        } else {
                            fps++;
                        }

                        //real stuff
                        updateEnemies();

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

        public void startTowerPlacingThread(){
            Thread towerPlacingThread = new Thread(new Runnable() {
                @Override
                public void run() { 
                    while(true){
                        //displays fps in console
                        if (fps == 30) {
                            System.out.println("FPS: " + fps);
                            fps = 0;
                        } else {
                            fps++;
                        }

                        //real stuff
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
            }, "towerPlacingThread");
            towerPlacingThread.start();
        }
    
        private void setupMouseListener() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("mouse clicked");
                    int x = e.getX() / 64;
                    int y = e.getY() / 64;
                    
                    if(MenuScreen.isTowerAttackOn()){
                        if (x >= 0 && x < size && y >= 0 && y < size) {
                            if (map[y][x] == 0) { // Only place a tower on empty tiles
                                map[y][x] = 2; // Mark the tile as occupied
                                towers.add(new Tower(x, y, img.getSubimage(19 * 64, 10 * 64, 64, 64), fireRate, range));
                            }
                        }
                        MenuScreen.flipTowerAttackValue();
                        useMoneyForAttackTower(getTowerCost());
                        MenuScreen.displayMoney();

                    }else if(map[y][x] == 2){
                        for (Tower tower : towers) {
                            if (tower.getX() / 64 == x && tower.getY() / 64 == y) {
                                showTowerMenu(x * 64, y * 64, tower);
                                return;
                            }
                        }
                    }else if(map[y][x] == 3){
                        for (EnergyTower energyTower : energyTowers) {
                            if (energyTower.getX() / 64 == x && energyTower.getY() / 64 == y) {
                                showEnergyTowerMenu(x * 64, y * 64, energyTower);
                                return;
                            }
                        }

                    }else if(MenuScreen.isEnergyTowerOn()){
                        if (x >= 0 && x < size && y >= 0 && y < size) {
                            if (map[y][x] == 0) {
                                map[y][x] = 3;
                                energyTowers.add(new EnergyTower(x, y, img.getSubimage(20 * 64, 10 * 64, 64, 64), 1));
                            }
                        }
                        MenuScreen.flipEnergyTowerValue();
                        useMoneyForEnergyTower(getEnergyTowerCost());
                        MenuScreen.displayMoney();
                    }
                }
            });
        }

        private void setupMouseMotionListener() {
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int x = e.getX() / 64;
                    int y = e.getY() / 64;

                    if (x >= 0 && x < size && y >= 0 && y < size) {
                        hoveredTileX = x;
                        hoveredTileY = y;
                        repaint(); // Repaint to update visual changes if necessary
                    } else {
                        hoveredTileX = -1;
                        hoveredTileY = -1; // Mouse is outside the grid
                    }

                    hoveredTower = null; // Reset hoveredTower
                    for (Tower tower : towers) {
                        if (new Rectangle(tower.getX(), tower.getY(), 64, 64).contains(x*64, y*64)) {
                            hoveredTower = tower;
                            break;
                        }
                    }
                }
            });
        }
             
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            double angleToEnemy = 0;
            float transparency = 0.5f;
            Graphics2D hover = (Graphics2D) g;
            
            if(MenuScreen.isEnergyTowerOn() || MenuScreen.isTowerAttackOn()){
                hover.setColor(Color.BLACK);
                hover.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
                hover.fillRect(hoveredTileX * 64, hoveredTileY * 64, 64, 64);
            }

            for(int y = 0; y < size; y++){
                for(int x = 0; x < size; x++){
                    g.drawImage(backGroundImages[y][x], x*64, y*64, null);
                }
            }
    
            for (Enemy enemy : new ArrayList<>(enemies)) {
                enemy.draw(g);
            }
    
            for(Tower tower : towers){
    
                Enemy nearestEnemy = tower.nearestEnemy(enemies);
                angleToEnemy = 0;
    
                currentTime = System.nanoTime();
                float deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0f;  // Convert to seconds
    
                if (nearestEnemy != null) {
                    angleToEnemy = tower.angleToNearestEnemy(nearestEnemy);
                    Projectile newProjectile = tower.shoot(40, angleToEnemy, deltaTime);
                    if (newProjectile != null) {
                        projectiles.add(newProjectile);
                    }
                }
                
                tower.draw(g, angleToEnemy);
            }

            if (hoveredTower != null) {
                int centerX = hoveredTower.getX() + 32;
                int centerY = hoveredTower.getY() + 32;
                int showrange = hoveredTower.getRange(); 
                g.setColor(new Color(255, 0, 0, 64)); // Semi-transparent blue
                g.fillOval(centerX - showrange, centerY - showrange, showrange * 2, showrange * 2);
                g.drawOval(centerX - showrange, centerY - showrange, showrange * 2, showrange * 2);
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

        public void makeEnemies(double spawnInterval){
            long currentSpawnTime = System.nanoTime();
            if (currentSpawnTime - lastSpawnTime >= spawnInterval) {
                if(enemyCounter < amountOfEnemies){
                    enemies.add(new Enemy(0, 64, img.getSubimage(20 * 64, 6 * 64, 64, 64)));
                    lastSpawnTime = currentSpawnTime;
                    enemyCounter++;
                }
            }
            if(enemyCounter == (amountOfEnemies)){
                allEnemiesMade = true;
            }
        }
    
        private void updateEnemies() {
            // Use an iterator to safely remove enemies
            Iterator<Enemy> iterator = enemies.iterator();
            while (iterator.hasNext()) {
                Enemy enemy = iterator.next();
                if(enemy.isEnemyDead()){
                    iterator.remove();
                    money++;
                    System.out.println("Enemy killed");
                }else if (enemy.update()) {
                    userLoseHP();
                    iterator.remove();
                    System.out.println("Enemy reached the end");
                }
                MenuScreen.displayMoney();
                checkWaveComplete();
            }
        }
    
        public void damageFirstEnemy(int damage) {
            if (!enemies.isEmpty()) {
                Enemy firstEnemy = enemies.get(0);
                if (firstEnemy.takeDamage(damage)) {
                    enemies.remove(firstEnemy); // Remove if health is 0
                    money += 5;
                    System.out.println("Enemy with text!");
                    checkWaveComplete();
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
            if(enemies.isEmpty() && allEnemiesMade){
                if(wave == 20){
                    Object[] options = {"Yes", "No"};
                    int gameContinue = JOptionPane.showOptionDialog(null, "You win! Would you like to play infinite mode?", "You Win",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options, options[0]);
                    
                    if(gameContinue == 0){
                        wave++;
                    }else if(gameContinue == 1) {
                        JOptionPane.showMessageDialog(null, "Thank you for playing WORDS TD :)", "WORDS TD", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }
                }else{
                    wave++;
                }
                MenuScreen.displayWave();
                enemies.clear();
                MenuScreen.flipPlayOn();
                MenuScreen.resetPlayButton();
                enemyCounter = 0;
                allEnemiesMade = false;
            }
        }

    public void userLoseHP(){
        userHP--;
        MenuScreen.displayHealth();
        if(userHP <= 0){
            JOptionPane.showMessageDialog(null, "You lost, better luck next time!", "You Lose", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    public static int getUserHP(){ return userHP; }
    public static int getWave(){ return wave; }

    private void showTowerMenu(int x, int y, Tower tower) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem upgradeOption = new JMenuItem("Upgrade Tower");
        upgradeOption.addActionListener(e -> showUpgradeMenu(x, y, tower));

        JMenuItem sellOption = new JMenuItem("Sell Tower");
        sellOption.addActionListener(e -> sellTower(x, y, tower));

        menu.add(upgradeOption);
        menu.add(sellOption);

        menu.show(this, x + 32, y + 32); // Adjusted to display below the tower
    }

    private void showUpgradeMenu(int x, int y, Tower tower){
        JPopupMenu upgradeMenu = new JPopupMenu();

        fireRateOption =  new JMenuItem("Upgrade atk speed: $15");
        fireRateOption.addActionListener(e -> lowerFireRate(tower));

        rangeOption =  new JMenuItem("Upgrade range: $4");
        rangeOption.addActionListener(e -> increaseRange(tower));

        upgradeMenu.add(fireRateOption);
        upgradeMenu.add(rangeOption);

        upgradeMenu.show(this, x + 64, y + 32);
    }

    public void sellTower(int x, int y, Tower tower){
        map[y/64][x/64] = 0;
        towers.remove(tower);
        money += towerCost - 5;
        towerCost -=5;
        MenuScreen.displayMoney();
        MenuScreen.displayTowerCost();
    }

    private void showEnergyTowerMenu(int x, int y, EnergyTower energyTower) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem sellOption = new JMenuItem("Sell Tower");
        sellOption.addActionListener(e -> sellEnergyTower(x, y, energyTower));

        menu.add(sellOption);

        menu.show(this, x + 32, y + 32); // Adjusted to display below the tower
    }

    public void sellEnergyTower(int x, int y, EnergyTower energyTower){
        map[y/64][x/64] = 0;
        energyTowers.remove(energyTower);
        money += energyTowerCost - 10;
        energyTowerCost -=10;
        MenuScreen.displayMoney();
        MenuScreen.displayEnergyTowerCost();
    }

    public void lowerFireRate(Tower tower) {
        if(tower.getFireRate() <= 0.31){
            JOptionPane.showOptionDialog(null, "You have maxed out this upgrade","Maxed Out Upgrade", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"OK"}, "OK");
        }else{
            if(money >= fireRateCost){
                    money -= fireRateCost;
                    tower.setFireRate(0.1);
                    System.out.println("tower fire rate: " + tower.getFireRate());
                    MenuScreen.displayMoney();
            }else{
                JOptionPane.showOptionDialog(null, "You do not have enough money to buy this upgrade","Insufficient Funds", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"OK"}, "OK");
            }
        }
    }

    public void increaseRange(Tower tower) {
        if (money >= rangeCost) {
            money -= rangeCost;
            tower.setRange(40);
            MenuScreen.displayMoney();
        }else{
            JOptionPane.showOptionDialog(null, "You do not have enough money to buy this upgrade","Insufficient Funds", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"OK"}, "OK");
        }
    }

    public static int getMoney(){ return money; }
    public static int getTowerCost(){ return towerCost; }
    public static int getEnergyTowerCost(){ return energyTowerCost; }
    public void useMoneyForAttackTower(int cost){ 
        money -= cost; 
        towerCost += 5;
        MenuScreen.displayMoney();
        MenuScreen.displayTowerCost();
    }
    public void useMoneyForEnergyTower(int cost){ 
        money -= cost; 
        energyTowerCost += 10;
        MenuScreen.displayMoney();
        MenuScreen.displayEnergyTowerCost();
    }
}

/*
 * add a boss every 10 waves
 * check if upgrade was clicked
 * determine if money is more than towerCost
 * place tower and take away cost from money
 * if money is lower than towerCost, display to the user that they do not have enough money
 * add different types of towers
 */
