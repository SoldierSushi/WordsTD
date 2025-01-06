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
    private String wordTyped = "";
    private String randomWord;
    private JLabel titleLabel;
    private JLabel wordToType;
    private static JLabel energyLabel;
    private JLabel currentWordTypingLabel;
    private JButton towerAttackButton;
    private JButton EnergyTowerButton;
    private JButton PlayButton;
    private static boolean towerAttackOn = false;
    private static boolean EnergyTowerOn = false;
    private static int energy = 10;
    private Runnable wordCompletedCallback;
    private Runnable startGameCallback;
    private static boolean play = false;

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

        //CurrentWordTypingLabel
        currentWordTypingLabel = new JLabel();
        currentWordTypingLabel.setBounds(840, 100, 50, 25);
        currentWordTypingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        currentWordTypingLabel.setAlignmentX(CENTER_ALIGNMENT);

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

        EnergyTowerButton = new JButton("Energy Tower");
        EnergyTowerButton.setBounds(840, 130, 50, 50);
        EnergyTowerButton.setForeground(Color.BLACK);
        EnergyTowerButton.setFont(new Font("Arial", Font.BOLD, 16));
        EnergyTowerButton.setAlignmentX(CENTER_ALIGNMENT);
        EnergyTowerButton.setFocusable(false);
        EnergyTowerButton.addActionListener(e -> {
            flipEnergyTowerValue();
        });

        PlayButton = new JButton("Play");
        PlayButton.setBounds(840, 750, 50, 50);
        PlayButton.setForeground(Color.BLACK);
        PlayButton.setFont(new Font("Arial", Font.BOLD, 16));
        PlayButton.setAlignmentX(CENTER_ALIGNMENT);
        PlayButton.setFocusable(false);
        PlayButton.addActionListener(e -> {
            if (startGameCallback != null && !play) {
                play = true;
                startGameCallback.run();
                PlayButton.setText("Running");
            }
        });
        
        //adding all to panel
        add(titleLabel);
        add(wordToType);
        add(towerAttackButton);
        add(energyLabel);
        add(EnergyTowerButton);
        add(currentWordTypingLabel);
        add(PlayButton);
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

    public static boolean isGameTrue(){ return play; }
    public static void flipPlayOn(){ play = !play; }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyClicked = e.getKeyCode();
        if(energy > 0){
            switch(keyClicked){
                case KeyEvent.VK_BACK_SPACE:
                    if(!wordTyped.isEmpty()){
                        wordTyped = wordTyped.substring(0, wordTyped.length()-1);
                    }
                    subtractEnergy();
                    break;
                default:
                    wordTyped += e.getKeyChar();
                    subtractEnergy();
                    break;
            }

            if(wordTyped.isEmpty()){
                currentWordTypingLabel.setText("");
            }else{
                changeLetterColor(wordTyped);
            }

            if(matchingWord()){ //callback needs to be run first because wordTyped resets to "" after being correct 
                if (wordCompletedCallback != null) {
                    wordCompletedCallback.run();
                }

                wordTyped = "";
                currentWordTypingLabel.setText("");
                getWord();
                wordToType.setText(randomWord);
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

    public void setWordCompletedCallback(Runnable callback) {
        this.wordCompletedCallback = callback;
    }

    public void startGameCallback(Runnable callback) {
        this.startGameCallback = callback;
    }

    public String getWordTyped() {
        return wordTyped;
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

    public void changeLetterColor(String wordTyped){
        boolean lastLetterCorrect = true;

        for(int i = 0; i < wordTyped.length(); i++){
            if(wordTyped.charAt(i) != randomWord.charAt(i)){
                currentWordTypingLabel.setForeground(Color.RED);
                lastLetterCorrect = false;
            }
            if(lastLetterCorrect){
                currentWordTypingLabel.setForeground(Color.BLACK);
            }
            currentWordTypingLabel.setText(wordTyped);
        }
    }

    public static void main(String[] args) {
        new MenuScreen();
    }
}
