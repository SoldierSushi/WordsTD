package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MenuScreen extends JPanel implements KeyListener{
    private ArrayList<String> words = new ArrayList<>();
    private String wordTyped = "";
    private String randomWord;
    private JLabel titleLabel;
    private JLabel wordToType;
    private JLabel wordToTypeTitle;
    private static JLabel energyLabel;
    private JLabel currentWordTypingLabel;
    private JLabel shopTitleLabel;
    private JButton towerAttackButton;
    private JButton EnergyTowerButton;
    private static JButton PlayButton;
    private static boolean towerAttackOn = false;
    private static boolean EnergyTowerOn = false;
    private static int energy = 10;
    private Runnable wordCompletedCallback;
    private Runnable startGameCallback;
    private static boolean play = false;

    public MenuScreen(){
        //panel stuff
        setLayout(null); // Stack components vertically
        setBounds(832, 0, 200, 860);
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        //titleLabel stuff
        titleLabel = new JLabel("MENU", SwingConstants.CENTER);
        titleLabel.setBounds(0, 0, 200, 50);
        titleLabel.setForeground(Color.BLACK); // Set text color
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 32));

        //wordToType stuff
        getWord();
        wordToType = new JLabel(randomWord, SwingConstants.CENTER);
        wordToType.setBounds(0, 150,200, 25);
        wordToType.setForeground(Color.BLACK);
        wordToType.setFont(new Font("Georgia", Font.BOLD, 16));

        wordToTypeTitle = new JLabel("TYPING", SwingConstants.CENTER);
        wordToTypeTitle.setBounds(0, 100, 200, 25);
        wordToTypeTitle.setForeground(Color.BLACK);
        wordToTypeTitle.setFont(new Font("Georgia", Font.BOLD, 24));

        //energy label
        energyLabel = new JLabel();
        energyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        energyLabel.setBounds(0, 125, 200, 25);
        energyLabel.setForeground(Color.BLACK);
        energyLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        displayEnergy();

        //CurrentWordTypingLabel
        currentWordTypingLabel = new JLabel("...", SwingConstants.CENTER);
        currentWordTypingLabel.setBounds(0, 175, 200, 25);
        currentWordTypingLabel.setFont(new Font("Georgia", Font.BOLD, 16));

        shopTitleLabel = new JLabel("SHOP", SwingConstants.CENTER);
        shopTitleLabel.setBounds(0, 400, 200, 25);
        shopTitleLabel.setForeground(Color.BLACK);
        shopTitleLabel.setFont(new Font("Georgia", Font.BOLD, 24));

        //towerAttackButton
        towerAttackButton = new JButton("Tower Attack ($10)");
        towerAttackButton.setBounds(0, 425, 200, 50);
        towerAttackButton.setForeground(Color.BLACK);
        towerAttackButton.setFont(new Font("Georgia", Font.BOLD, 16));
        towerAttackButton.setFocusable(false);
        towerAttackButton.addActionListener(e -> {
            flipTowerAttackValue();
        });

        EnergyTowerButton = new JButton("Energy Tower ($100)");
        EnergyTowerButton.setBounds(0, 475, 200, 50);
        EnergyTowerButton.setForeground(Color.BLACK);
        EnergyTowerButton.setFont(new Font("Georgia", Font.BOLD, 16));
        EnergyTowerButton.setFocusable(false);
        EnergyTowerButton.addActionListener(e -> {
            flipEnergyTowerValue();
        });

        PlayButton = new JButton("Play");
        PlayButton.setBounds(0, 732, 200, 100);
        PlayButton.setForeground(Color.BLACK);
        PlayButton.setFont(new Font("Georgia", Font.BOLD, 16));
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
        add(wordToTypeTitle);
        add(towerAttackButton);
        add(energyLabel);
        add(EnergyTowerButton);
        add(currentWordTypingLabel);
        add(PlayButton);
        add(shopTitleLabel);
    }

    //used in gamescreen for towerAttack
    public static boolean isTowerAttackOn(){return towerAttackOn;}
    public static void flipTowerAttackValue(){towerAttackOn = !towerAttackOn;}

    //used in gamescreen for EnergyTower
    public static boolean isEnergyTowerOn(){return EnergyTowerOn;}
    public static void flipEnergyTowerValue(){EnergyTowerOn = !EnergyTowerOn;}

    public static void addEnergy(){ energy++; }
    public static void displayEnergy(){ System.out.println("Energy to display: " + energy); energyLabel.setText("Typing Energy: " + energy); }

    public static boolean isGameTrue(){ return play; }
    public static void flipPlayOn(){ play = !play; }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyClicked = e.getKeyCode();
        if(energy > 0){
                if(keyClicked == KeyEvent.VK_BACK_SPACE){
                    if(!wordTyped.isEmpty()){
                        wordTyped = wordTyped.substring(0, wordTyped.length()-1);
                    }
                    energy--;
                }else{
                    wordTyped += e.getKeyChar();
                    energy--;
                }

            if(wordTyped.isEmpty()){
                currentWordTypingLabel.setText("...");
            }else{
                changeLetterColor(wordTyped);
            }

            if(matchingWord()){ //callback needs to be run first because wordTyped resets to "" after being correct 
                if (wordCompletedCallback != null) {
                    wordCompletedCallback.run();
                }

                wordTyped = "";
                currentWordTypingLabel.setText("...");
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

        for(int i = 0; i < Math.min(wordTyped.length(), randomWord.length()); i++){
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

    public static void resetPlayButton(){
        PlayButton.setText("Play");
    }

    public static void main(String[] args) {
        new MenuScreen();
    }
}
