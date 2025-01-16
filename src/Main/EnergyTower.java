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

    /*
    Description: creates energy
    Pre-Condition: called every time an energy tower generates energy: requires 1 float
    Post-Condition: adds energy and displays it to the user
    */
    public void harvestEnergy(float deltaTimeEnergy){
        timeSinceLastEnergy += deltaTimeEnergy;
        if(MenuScreen.isGameTrue()){
            if(timeSinceLastEnergy >= energyRate){
                MenuScreen.addEnergy();
                MenuScreen.displayEnergy();
                timeSinceLastEnergy = 0;
            }
        }
    }

    /*
    Description: gets the energy tower's x coordinate
    Pre-Condition: called when the energy tower is placed or when it is clicked
    Post-Condition: returns towerX * 64 because each displayed tile is 64 pixels 
    */
    public int getX() { return towerX*64; }

    /*
    Description: gets the energy tower's y coordinate
    Pre-Condition: called when the energy tower is placed or when it is clicked
    Post-Condition: returns towerY * 64 because each displayed tile is 64 pixels 
    */
    public int getY() { return towerY*64; }

    /*
    Description: draws the energy tower
    Pre-Condition: called every time paintComponent() runs
    Post-Condition: draws the energy tower using the towerX and towerY coordinates
    */
    public void draw(Graphics g) {
        int towerScreenX = towerX * 64;
        int towerScreenY = towerY * 64;

        g.drawImage(image, towerScreenX, towerScreenY, null);
    }
}