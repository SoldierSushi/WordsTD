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
    private static JLabel userHPLabel;
    private static JLabel waveLabel;
    private static JLabel moneyLabel;
    private static JButton towerAttackButton;
    private static JButton EnergyTowerButton;
    private static JButton PlayButton;
    private static boolean towerAttackOn = false;
    private static boolean EnergyTowerOn = false;
    private static int energy = 10;
    private Runnable wordCompletedCallback;
    private Runnable startGameCallback;
    private static boolean play = false;
    private static TowerType selectedTowerType = null;

    public MenuScreen(){
        setLayout(null);
        setBounds(832, 0, 200, 860);
        requestFocusInWindow();
        addKeyListener(this);

        titleLabel = new JLabel("MENU", SwingConstants.CENTER);
        titleLabel.setBounds(0, 0, 200, 50);
        titleLabel.setForeground(Color.BLACK); // Set text color
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 32));

        getWord();
        wordToType = new JLabel(randomWord, SwingConstants.CENTER);
        wordToType.setBounds(0, 150,200, 25);
        wordToType.setForeground(Color.BLACK);
        wordToType.setFont(new Font("Georgia", Font.BOLD, 16));

        wordToTypeTitle = new JLabel("TYPING", SwingConstants.CENTER);
        wordToTypeTitle.setBounds(0, 100, 200, 25);
        wordToTypeTitle.setForeground(Color.BLACK);
        wordToTypeTitle.setFont(new Font("Georgia", Font.BOLD, 24));

        energyLabel = new JLabel();
        energyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        energyLabel.setBounds(0, 125, 200, 25);
        energyLabel.setForeground(Color.BLACK);
        energyLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        displayEnergy();

        currentWordTypingLabel = new JLabel("...", SwingConstants.CENTER);
        currentWordTypingLabel.setBounds(0, 175, 200, 25);
        currentWordTypingLabel.setFont(new Font("Georgia", Font.BOLD, 16));

        shopTitleLabel = new JLabel("SHOP", SwingConstants.CENTER);
        shopTitleLabel.setBounds(0, 400, 200, 25);
        shopTitleLabel.setForeground(Color.BLACK);
        shopTitleLabel.setFont(new Font("Georgia", Font.BOLD, 24));

        userHPLabel = new JLabel();
        userHPLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userHPLabel.setBounds(0, 700, 200, 25);
        userHPLabel.setForeground(Color.BLACK);
        userHPLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        displayHealth();

        waveLabel = new JLabel();
        waveLabel.setHorizontalAlignment(SwingConstants.CENTER);
        waveLabel.setBounds(0, 675, 200, 25);
        waveLabel.setForeground(Color.BLACK);
        waveLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        displayWave();

        moneyLabel = new JLabel();
        moneyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        moneyLabel.setBounds(0, 525, 200, 25);
        moneyLabel.setForeground(Color.BLACK);
        moneyLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        displayMoney();

        //towerAttackButton
        towerAttackButton = new JButton();
        towerAttackButton.setBounds(0, 425, 200, 50);
        towerAttackButton.setForeground(Color.BLACK);
        towerAttackButton.setFont(new Font("Georgia", Font.BOLD, 16));
        towerAttackButton.setFocusable(false);
        displayTowerCost();
        towerAttackButton.addActionListener(e -> {
            if(GameScreen.getMoney() >= GameScreen.getTowerCost() || isTowerAttackOn()){
                flipTowerAttackValue();
                selectedTowerType = TowerType.ATTACK;
            }
        });

        EnergyTowerButton = new JButton();
        EnergyTowerButton.setBounds(0, 475, 200, 50);
        EnergyTowerButton.setForeground(Color.BLACK);
        EnergyTowerButton.setFont(new Font("Georgia", Font.BOLD, 16));
        EnergyTowerButton.setFocusable(false);
        displayEnergyTowerCost();
        EnergyTowerButton.addActionListener(e -> {
            if(GameScreen.getMoney() >= GameScreen.getEnergyTowerCost() || isEnergyTowerOn()){
                flipEnergyTowerValue();
                selectedTowerType = TowerType.ENERGY;
            }
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
        add(userHPLabel);
        add(waveLabel);
        add(moneyLabel);
    }

    public static TowerType getTowerType(){
        return selectedTowerType;
    }

    /*
    Description: sets the focus to be on the current window, this allows for typing checks without using a textField
    Pre-Condition: called only when a new instance of Game constructor is made: requires 1 boolean
    Post-Condition: sets the focus to be on the current window
    */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            requestFocusInWindow(); // Re-request focus when visible
        }
    }

    /*
    Description: checks if a new attack tower is in the process of being placed
    Pre-Condition: called when towerAttackButton is clicked and whenever the mouse has been clicked
    Post-Condition: returns the boolean towerAttackOn (true or false)
    */
    public static boolean isTowerAttackOn(){return towerAttackOn;}

    /*
    Description: flips towerAttackOn to the opposite state
    Pre-Condition: called when towerAttackButton is clicked as well as after a tower has been placed
    Post-Condition: flips the towerAttackOn value from true -> false and vice versa
    */
    public static void flipTowerAttackValue(){ 
        if(EnergyTowerOn){
            EnergyTowerOn = !EnergyTowerOn;
        }
        towerAttackOn = !towerAttackOn;
    }

    /*
    Description: checks if a new energy tower is in the process of being placed
    Pre-Condition: called when EnergyTowerButton is clicked and whenever the mouse has been clicked
    Post-Condition: returns the boolean EnergyTowerOn (true or false)
    */
    public static boolean isEnergyTowerOn(){return EnergyTowerOn;}

    /*
    Description: flips EnergyTowerOn to the opposite state
    Pre-Condition: called when EnergyTowerButton is clicked as well as after a tower has been placed
    Post-Condition: flips the EnergyTowerOn value from true -> false and vice versa
    */
    public static void flipEnergyTowerValue(){ 
        if(towerAttackOn){
            towerAttackOn = !towerAttackOn;
        }
        EnergyTowerOn = !EnergyTowerOn; 
    }

    /*
    Description: adds energy
    Pre-Condition: called by energy towers
    Post-Condition: adds 1 to the user's energy
    */
    public static void addEnergy(){ energy++; }

    /*
    Description: displays energy to the user
    Pre-Condition: called every time energy is altered
    Post-Condition: sets the text of energyLabel to the current amount of energy
    */
    public static void displayEnergy(){ energyLabel.setText("Typing Energy: " + energy); }

    /*
    Description: displays health to the user
    Pre-Condition: called every time health is subtracted
    Post-Condition: sets the text of userHPLabel to the current amount of health
    */
    public static void displayHealth(){ userHPLabel.setText("Health: " + GameScreen.getUserHP()); }

    /*
    Description: displays wave to the user
    Pre-Condition: called every time wave increases
    Post-Condition: sets the text of waveLabel to the current/upcoming wave
    */
    public static void displayWave(){ waveLabel.setText("Wave: " + GameScreen.getWave());}

    /*
    Description: displays money to the user
    Pre-Condition: called every time money is altered
    Post-Condition: sets the text of moneyLabel to the current amount of money
    */
    public static void displayMoney(){ moneyLabel.setText("Money: $" + GameScreen.getMoney()); }

    /*
    Description: displays attack tower cost to the user
    Pre-Condition: called every tower cost is changed
    Post-Condition: sets the text of towerAttackButton to the current attack tower cost
    */
    public static void displayTowerCost(){ towerAttackButton.setText("Attack Tower : $" + GameScreen.getTowerCost()); }

    /*
    Description: displays energy tower cost to the user
    Pre-Condition: called every time energy tower cost is changed
    Post-Condition: sets the text of EnergyTowerButton to the current energy tower cost
    */
    public static void displayEnergyTowerCost(){ EnergyTowerButton.setText("Energy Tower : $" + GameScreen.getEnergyTowerCost()); }

    /*
    Description: checks if the game is on to start the next wave
    Pre-Condition: called in GameScreen to check whether or not the game can start running
    Post-Condition: returns the boolean play
    */
    public static boolean isGameTrue(){ return play; }

    /*
    Description: flips the state of the boolean play
    Pre-Condition: called when PlayButton is pressed and when the round is complete
    Post-Condition: inverts the value of play from false -> true and vice versa
    */
    public static void flipPlayOn(){ play = !play; }

    /*
    Description: checks if any keys were pressed by the user
    Pre-Condition: only works if focus is set to the current window: requires 1 KeyEvent
    Post-Condition: adds letter to wordTyped string and checks if it matches with the target word
    */
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

    /*
    Description: checks if the word the user typed is the same as the target word
    Pre-Condition: called every time a key is pressed
    Post-Condition: returns true of false based on if the words match or not
    */
    public boolean matchingWord(){
        for(int i = 0; i < randomWord.length(); i++){
            if(!wordTyped.equals(randomWord)){
                return false;
            }
        }
        return true;
    }

    /*
    Description: sets a callback in GameScreen for if a word is complete
    Pre-Condition: initialized in GameScreen constructor, called when wordTyped is the same as the target word
    Post-Condition: calls the callback located in GameScreen to execute damageFirstEnemy()
    */
    public void setWordCompletedCallback(Runnable callback) {
        this.wordCompletedCallback = callback;
    }

    /*
    Description: sets a callback in GameScreen for if the next wave was started
    Pre-Condition: initialized in GameScreen constructor, called when PlayButton is pressed
    Post-Condition: calls the callback located in GameScreen to execute startGameThread()
    */
    public void startGameCallback(Runnable callback) {
        this.startGameCallback = callback;
    }

    /*
    Description: gets the word typed by the user
    Pre-Condition: called in GameScreen
    Post-Condition: returns wordTyped to GameScreen to check the length of the word (function does not serve much purpose in current game)
    */
    public String getWordTyped() {
        return wordTyped;
    }

    /*
    Description: keyPressed function does not work if this method is not initialized somehow
    Pre-Condition: N/A
    Post-Condition: N/A
    */
    @Override
    public void keyReleased(KeyEvent e) {}

    /*
    Description: keyPressed function does not work if this method is not initialized somehow
    Pre-Condition: N/A
    Post-Condition: N/A
    */
    @Override
    public void keyTyped(KeyEvent e) {}

    /*
    Description: gets a random word for the user to type
    Pre-Condition: called when the game is first started as well as every time matchingWord() returns true
    Post-Condition: sets the String randomWord using randomWord()
    */
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

    /*
    Description: picks a random word from text file
    Pre-Condition: called when getWord() is called
    Post-Condition: returns a String of the selected random word
    */
    public String randomWord() {
        int index = (int) (Math.random() * words.size());
        return words.get(index);
    }

    /*
    Description: changes the letter color if a word is spelled incorrectly
    Pre-Condition: called once a key is pressed: requires 1 String
    Post-Condition: changes the color of the letters to red for incorrect, stays black for correct
    */
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

    /*
    Description: resets the play button text
    Pre-Condition: called when the current wave has been completed
    Post-Condition: changes PlayButton text from "running" to back to "play"
    */
    public static void resetPlayButton(){
        PlayButton.setText("Play");
    }
}
