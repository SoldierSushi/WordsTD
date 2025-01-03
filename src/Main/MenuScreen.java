package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MenuScreen extends JPanel implements KeyListener{
    private ArrayList<String> words = new ArrayList<>();
    private String wordTyped;
    private String randomWord;
    private JLabel titleLabel;
    private JLabel wordToType;
    private static JLabel energyLabel;
    private JButton towerAttackButton;
    private JButton EnergyTowerButton;
    private static boolean towerAttackOn = false;
    private static boolean EnergyTowerOn = false;
    private static int energy = 10;

    public MenuScreen(){
        //panel stuff
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Stack components vertically
        setBounds(832, 0, 200, 860);
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        //titleLabel stuff
        titleLabel = new JLabel("MENU");
        titleLabel.setBounds(840, 10, 180, 30);
        titleLabel.setForeground(Color.BLACK); // Set text color
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        //wordToType stuff
        getWord();
        wordToType = new JLabel(randomWord);
        wordToType.setBounds(840, 40, 50, 25);
        wordToType.setForeground(Color.BLACK);
        wordToType.setFont(new Font("Arial", Font.BOLD, 16));
        wordToType.setAlignmentX(CENTER_ALIGNMENT);

        //energy label
        energyLabel = new JLabel();
        energyLabel.setBounds(840, 100, 50, 25);
        energyLabel.setForeground(Color.BLACK);
        energyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        energyLabel.setAlignmentX(CENTER_ALIGNMENT);
        displayEnergy();

        //towerAttackButton
        towerAttackButton = new JButton("Tower Attack");
        towerAttackButton.setBounds(840, 70, 50, 50);
        towerAttackButton.setForeground(Color.BLACK);
        towerAttackButton.setFont(new Font("Arial", Font.BOLD, 16));
        towerAttackButton.setAlignmentX(CENTER_ALIGNMENT);
        towerAttackButton.setFocusable(false);
        towerAttackButton.addActionListener(e -> {
            flipTowerAttackValue();
        });

        EnergyTowerButton = new JButton("Tower Attack");
        EnergyTowerButton.setBounds(840, 130, 50, 50);
        EnergyTowerButton.setForeground(Color.BLACK);
        EnergyTowerButton.setFont(new Font("Arial", Font.BOLD, 16));
        EnergyTowerButton.setAlignmentX(CENTER_ALIGNMENT);
        EnergyTowerButton.setFocusable(false);
        EnergyTowerButton.addActionListener(e -> {
            flipEnergyTowerValue();
        });
        
        //adding all to panel
        add(titleLabel);
        add(wordToType);
        add(towerAttackButton);
        add(energyLabel);
        add(EnergyTowerButton);
    }

    //used in gamescreen for towerAttack
    public static boolean isTowerAttackOn(){return towerAttackOn;}
    public static void flipTowerAttackValue(){towerAttackOn = !towerAttackOn;}

    //used in gamescreen for EnergyTower
    public static boolean isEnergyTowerOn(){return EnergyTowerOn;}
    public static void flipEnergyTowerValue(){EnergyTowerOn = !EnergyTowerOn;}

    public static void subtractEnergy(){ energy--; }
    public static void addEnergy(){ energy++; }
    public static void displayEnergy(){ energyLabel.setText("" + energy); }

    @Override
    public void keyPressed(KeyEvent e) {
        if(energy > 0){
            switch(e.getKeyCode()){
                case KeyEvent.VK_BACK_SPACE:
                    if(!wordTyped.isEmpty()){
                        wordTyped = wordTyped.substring(0, wordTyped.length()-1);
                    }
                    System.out.println(wordTyped);
                    subtractEnergy();
                    break;
                case KeyEvent.VK_ENTER:
                    if(matchingWord()){
                        getWord();
                        wordToType.setText(randomWord);
                        wordTyped = "";
                    }
                    break;
                default:
                    wordTyped += e.getKeyChar();
                    System.out.println(wordTyped);
                    subtractEnergy();
                    break;
            }
        }
        displayEnergy();
    }

    public boolean matchingWord(){
        for(int i = 0; i < randomWord.length(); i++){
            if(!wordTyped.equals(randomWord)){
                return false;
            }
        }
        return true;
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public void getWord(){
        try {
            Scanner input = new Scanner(new File("src/Main/words.txt"));
            while (input.hasNextLine()) {
                words.add(input.nextLine().trim());
            }
            input.close();
        } catch (Exception e) {
            System.out.println("src/Main/words.txt");
        }
        
        randomWord = randomWord();
        wordTyped = "";
        System.out.println(randomWord);
    }

    public String randomWord() {
        int index = (int) (Math.random() * words.size());
        return words.get(index);
    }

    public static void main(String[] args) {
        new MenuScreen();
    }
}
