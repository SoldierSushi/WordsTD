package Main;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class EnergyTower {
    private BufferedImage image;
    private int towerY;
    private int towerX;
    private double energyRate;
    private float timeSinceLastEnergy;

    public EnergyTower(int towerX, int towerY, BufferedImage image, double energyRate){
        this.towerX = towerX;
        this.towerY = towerY;
        this.image = image;
        this.energyRate = energyRate;
        this.timeSinceLastEnergy = 0;
    }

    public void harvestEnergy(float deltaTimeEnergy){
        timeSinceLastEnergy += deltaTimeEnergy;
        if(timeSinceLastEnergy >= energyRate){
            MenuScreen.addEnergy();
            MenuScreen.displayEnergy();
            timeSinceLastEnergy = 0;
        }
    }

    public void draw(Graphics g) {
        int towerScreenX = towerX * 64;
        int towerScreenY = towerY * 64;

        g.drawImage(image, towerScreenX, towerScreenY, null);
    }
}
