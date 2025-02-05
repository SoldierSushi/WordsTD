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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    private static int wave = 1;
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
    private static int money = 200;
    private static int towerCost = 20;
    private static int energyTowerCost = 100;
    private Tower hoveredTower = null;
    private JMenuItem fireRateOption;
    private JMenuItem rangeOption;
    private boolean gameON = false;
    
    public GameScreen(BufferedImage img, MenuScreen menuScreen) {
        this.img = img;

        preloadBackground();
        startGameThread();
        setupMouseListener();
        setupMouseMotionListener();

        setBounds(0,0, 832, 860);
        lastUpdateTime = System.nanoTime();
        
        menuScreen.setWordCompletedCallback(() -> damageFirstEnemy(menuScreen.getWordTyped().length()));
        menuScreen.startGameCallback(this::startGameThread);
        gameON = true;
    }

    /*
    Description: starts the game loop, updating game components in specified intervals
    Pre-Condition: called by GameScreen constructor, also called when recieving callback from menuScreen
    Post-Condition: creates a new Thread gameThread and runs game functions
    */
    public void startGameThread(){
        Thread gameThread = new Thread(new Runnable() {
            @Override
            public void run() { 
                fps = 0;
                double spawnSpeed = 1.6 * Math.pow(0.9, wave);
                amountOfEnemies += 3 * (int) Math.pow(1.1, wave);
                System.out.println("Spawn Speed: " + spawnSpeed);
                System.out.println("Enemies to spawn: " + amountOfEnemies);
                float spawnInterval = 1000000000 * (float) spawnSpeed ; // 1 second in nanoseconds
                lastSpawnTime = System.nanoTime();
                while(gameON){
                    if (fps == 30) {
                        System.out.println("FPS: " + fps);
                        fps = 0;
                    } else {
                        fps++;
                    }

                    if(MenuScreen.isGameTrue()){
                        makeEnemies(spawnInterval);
                        updateEnemies();
                    }

                    repaint();

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

    /*
    Description: checks mouse clicks for interaction during game
    Pre-Condition: called by GameScreen construct
    Post-Condition: sets a MouseAdapter to watch for any mouse interactions from user for the entire duration of game
    */
    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / 64;
                int y = e.getY() / 64;
                System.out.println("selected tower: " + MenuScreen.getTowerType());

                if (!isValidTile(x, y)) {
                    return;
                }

                if (MenuScreen.getTowerType() == null) {
                    handleTowerSelection(x, y);
                } else {
                    placeTower(x, y, MenuScreen.getTowerType());
                }
            }
        });
    }

    private boolean isValidTile(int x, int y){
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    private void placeTower(int x, int y, TowerType towerType){
        switch (towerType){
            case ATTACK:
                map[y][x] = 2;
                towers.add(new Tower(x, y, img.getSubimage(20 * 64, 8 * 64, 64, 64), fireRate, range));
                MenuScreen.setTowerType(null);
                useMoneyForAttackTower(getTowerCost());
                MenuScreen.displayMoney();
                break;
            case ENERGY:
                map[y][x] = 3;
                energyTowers.add(new EnergyTower(x, y, img.getSubimage(21 * 64, 8 * 64, 64, 64), 3));
                MenuScreen.setTowerType(null);
                useMoneyForEnergyTower(getEnergyTowerCost());
                MenuScreen.displayMoney();
                break;
            default:
                return;
        }
    }

    private void handleTowerSelection(int x, int y) {
    Map<Integer, Runnable> selectionActions = new HashMap<>();
    
    selectionActions.put(2, () -> towers.stream()
        .filter(tower -> tower.getX() / 64 == x && tower.getY() / 64 == y)
        .findFirst()
        .ifPresent(tower -> showTowerMenu(x * 64, y * 64, tower))
    );

    selectionActions.put(3, () -> energyTowers.stream()
        .filter(energyTower -> energyTower.getX() / 64 == x && energyTower.getY() / 64 == y)
        .findFirst()
        .ifPresent(energyTower -> showEnergyTowerMenu(x * 64, y * 64, energyTower))
    );

    selectionActions.getOrDefault(map[y][x], () -> {}).run();
    }

    /*
    Description: checks if the user is hovering over tower
    Pre-Condition: called by GameScreen constructor
    Post-Condition: uses MouseAdapter to set hoveredTower to the selected tower
    */
    private void setupMouseMotionListener() {
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX() / 64;
                int y = e.getY() / 64;

                if (x >= 0 && x < size && y >= 0 && y < size) {
                    hoveredTileX = x;
                    hoveredTileY = y;
                    repaint();
                } else { // Mouse is outside the grid, no need to repaint
                    hoveredTileX = -1;
                    hoveredTileY = -1;
                }

                hoveredTower = null;

                for (Tower tower : towers) {
                    if (new Rectangle(tower.getX(), tower.getY(), 64, 64).contains(x*64, y*64)) {
                        hoveredTower = tower;
                        break;
                    }
                }
            }
        });
    }
            
    /*
    Description: paints all images that are in the game onto
    Pre-Condition: displays certain game components only if conditions are met (ex. hoveredTower != null)
    Post-Condition: alters and displays game components on the screen
    */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        double angleToEnemy = 0;
        float transparency = 0.5f;
        Graphics2D hover = (Graphics2D) g;
        
        if(MenuScreen.getTowerType() != null){
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
            float deltaTimeEnergy = (currentTimeEnergy - lastUpdateTimeEnergy) / 1_000_000_000.0f;
            energyTower.harvestEnergy(deltaTimeEnergy);
            energyTower.draw(g);
        }
        lastUpdateTimeEnergy = currentTimeEnergy;

        if (projectiles != null) {
            updateProjectiles(g);
        }
    }

    /*
    Description: preloads all background images to reduce cpu usage
    Pre-Condition: run by GameScreen constructor
    Post-Condition: based on the integers on map grid, sets background to specified images
    */
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

    /*
    Description: creates new enemies
    Pre-Condition: run every time a new instance of gameThread is created, requires 1 double
    Post-Condition: adds an enemy to a the ArrayList enemies to keep track of their actions
    */
    public void makeEnemies(double spawnInterval){
        long currentSpawnTime = System.nanoTime();
        if (currentSpawnTime - lastSpawnTime >= spawnInterval) {
            if(enemyCounter < amountOfEnemies){
                enemies.add(new Enemy(0, 64, img.getSubimage(18 * 64, 11 * 64, 64, 64)));
                lastSpawnTime = currentSpawnTime;
                enemyCounter++;
            }
        }
        if(enemyCounter == (amountOfEnemies)){
            allEnemiesMade = true;
        }
    }

    /*
    Description: updates enemy positions and states
    Pre-Condition: called once per frame while gameThread is active
    Post-Condition: updates enemy position, removes enemy from ArrayList enemies if needed, then checks if wave completed
    */
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

    /*
    Description: damages the frontmost enemy if a word was typed correctly
    Pre-Condition: called once a word has been typed correctly: requires 1 int
    Post-Condition: removes first iteration of enemy in ArrayList enemies
    */
    public void damageFirstEnemy(int damage) {
        if (!enemies.isEmpty()) {
            Enemy firstEnemy = enemies.get(0);
            if (firstEnemy.takeDamage(damage)) {
                enemies.remove(firstEnemy);
                money += 5;
                System.out.println("Enemy with text!");
                checkWaveComplete();
            }
        }
    }

    /*
    Description: updates the projectiles motion
    Pre-Condition: called every time paintComponent is run: requires 1 Graphics
    Post-Condition: changes each projectile's position on the game, also checks if out of bounds or hit an enemy
    */
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

    /*
    Description: checks if the wave has been complete
    Pre-Condition: called every time an enemy is killed and if all the enemies that wave were created
    Post-Condition: checks the win condition and increases the wave by 1, also resets variables
    */
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

    /*
    Description: subtract health from the user
    Pre-Condition: called only if the enemy completes the entire track
    Post-Condition: subtracts 1 health from user and checks lose condition
    */
    public void userLoseHP(){
        userHP--;
        MenuScreen.displayHealth();
        if(userHP <= 0){
            JOptionPane.showMessageDialog(null, "You lost, better luck next time!", "You Lose", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    /*
    Description: gets the user's health
    Pre-Condition: called when the user loses health
    Post-Condition: returns userHP to MenuScreen to display health
    */
    public static int getUserHP(){ return userHP; }

    /*
    Description: gets the current wave
    Pre-Condition: called only if checkWaveComplete returns true
    Post-Condition: returns wave to MenuScreen to display wave
    */
    public static int getWave(){ return wave; }

    /*
    Description: shows the tower menu if tower is clicked
    Pre-Condition: called only if mouse is clicked onto a tower: requires 2 int, 1 Tower (selected tower)
    Post-Condition: shows a JPopupMenu menu originating at the selected tower's center
    */
    private void showTowerMenu(int x, int y, Tower tower) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem upgradeOption = new JMenuItem("Upgrade Tower");
        upgradeOption.addActionListener(e -> showUpgradeMenu(x, y, tower));

        JMenuItem sellOption = new JMenuItem("Sell Tower");
        sellOption.addActionListener(e -> sellTower(x, y, tower));

        menu.add(upgradeOption);
        menu.add(sellOption);

        menu.show(this, x + 32, y + 32);
    }

    /*
    Description: shows the upgrade options for tower
    Pre-Condition: called only if JMenuItem upgradeOption is selected: requires 2 int, 1 Tower
    Post-Condition: shows new JPopupMenu with upgrade options
    */
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

    /*
    Description: sells the selected tower
    Pre-Condition: called only if sellOption is selected: requires 2 int, 1 Tower
    Post-Condition: sets the towers location in the map grid to 0 (nothing placed there) and changes prices to be cheaper
    */
    public void sellTower(int x, int y, Tower tower){
        map[y/64][x/64] = 0;
        towers.remove(tower);
        money += towerCost - 5;
        towerCost -=5;
        MenuScreen.displayMoney();
        MenuScreen.displayTowerCost();
    }

    /*
    Description: shows the energy tower menu if clicked
    Pre-Condition: called only if mouse is clicked onto an existing energy tower: requires 2 int, 1 EnergyTower
    Post-Condition: creates a JPopupMenu menu originating at the center of the energy tower with the option to sell
    */
    private void showEnergyTowerMenu(int x, int y, EnergyTower energyTower) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem sellOption = new JMenuItem("Sell Tower");
        sellOption.addActionListener(e -> sellEnergyTower(x, y, energyTower));

        menu.add(sellOption);

        menu.show(this, x + 32, y + 32);
    }

    /*
    Description: sells the selected energy tower
    Pre-Condition: called only if JMenuOption sellOption is selected: requires 2 int, 1 EnergyTower
    Post-Condition: sets the energy tower's location on the map grid to 0 and changes prices to be cheaper
    */
    public void sellEnergyTower(int x, int y, EnergyTower energyTower){
        map[y/64][x/64] = 0;
        energyTowers.remove(energyTower);
        money += energyTowerCost - 10;
        energyTowerCost -=10;
        MenuScreen.displayMoney();
        MenuScreen.displayEnergyTowerCost();
    }

    /*
    Description: lowers the "reload" time for attack towers
    Pre-Condition: called only if JMenuItem fireRateOption is selected: requires 1 Tower
    Post-Condition: lowers the selected tower's fireRate by 0.1, uses money and displays new money amount
    */
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

    /*
    Description: increases the range of the attack towers
    Pre-Condition: called only if JMenuItem rangeOption is selected: requires 1 Tower
    Post-Condition: increases the selected tower's range by 40, uses money and displays new money amount
    */
    public void increaseRange(Tower tower) {
        if (money >= rangeCost) {
            money -= rangeCost;
            tower.setRange(40);
            MenuScreen.displayMoney();
        }else{
            JOptionPane.showOptionDialog(null, "You do not have enough money to buy this upgrade","Insufficient Funds", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"OK"}, "OK");
        }
    }

    /*
    Description: gets the money amount
    Pre-Condition: called every time a transaction is performed in game
    Post-Condition: returns the int money to display in MenuScreen
    */
    public static int getMoney(){ return money; }

    /*
    Description: gets the cost of the attack tower
    Pre-Condition: called every time a new tower is placed
    Post-Condition: returns the int towerCost to display in MenuScreen
    */
    public static int getTowerCost(){ return towerCost; }

    /*
    Description: gets the cost of the energy tower
    Pre-Condition: called every time a new energy tower is placed
    Post-Condition: returns the int energyTowerCost to display in MenuScreen
    */
    public static int getEnergyTowerCost(){ return energyTowerCost; }

    /*
    Description: uses user's money to buy attack tower
    Pre-Condition: called only if attack tower is placed onto the map: requires 1 int
    Post-Condition: subtracts the user's money by the cost of the attack tower, displays new money and towerCost values in MenuScreen
    */
    public void useMoneyForAttackTower(int cost){ 
        money -= cost; 
        towerCost += 5;
        MenuScreen.displayMoney();
        MenuScreen.displayTowerCost();
    }

    /*
    Description: uses user's money to buy energy tower
    Pre-Condition: called only if energy tower is placed onto the map: requires 1 int
    Post-Condition: subtracts the user's money by the cost of the energy tower, displays new money and towerCost values in MenuScreen
    */
    public void useMoneyForEnergyTower(int cost){ 
        money -= cost; 
        energyTowerCost += 10;
        MenuScreen.displayMoney();
        MenuScreen.displayEnergyTowerCost();
    }
}
/*
 * If I had extra time I would:
 * organize code (move all jumbled code in GameScreen to their respective classes)
 * add boss every 10 waves
 * add different types of towers
 * add different types of enemies
 * have enemies have different amounts of health
 */