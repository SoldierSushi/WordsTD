package Main;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class EnergyTower {
    private BufferedImage image;
    private int towerY;
    private int towerX;
    private int fps = 0;

    public EnergyTower(int towerX, int towerY, BufferedImage image){
        this.towerX = towerX;
        this.towerY = towerY;
        this.image = image;

        //makeEnergyGameThread();
    }

    /*public void makeEnergyGameThread(){
        Thread makeEnergy = new Thread(new Runnable() {
            @Override
            public void run(){

                if(fps == 15){
                    System.out.println("works");
                    MenuScreen.addEnergy();
                    MenuScreen.displayEnergy();
                }
                fps++;

                long frameTime = System.nanoTime();
                    try {
                        long sleepTime = Math.max(0, 33 - (System.nanoTime() - frameTime) / 1_000_000);
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }, "makeEnergy");
        makeEnergy.start();
    }*/

    public void draw(Graphics g) {
        int towerScreenX = towerX * 64;
        int towerScreenY = towerY * 64;

        g.drawImage(image, towerScreenX, towerScreenY, null);
    }
}
